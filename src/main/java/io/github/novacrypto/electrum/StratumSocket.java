/*
 *  ElectrumClientRx
 *  Copyright (C) 2017 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/ElectrumClientRx
 *  You can contact the authors via github issues.
 */

package io.github.novacrypto.electrum;

import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public final class StratumSocket {
    private final PrintWriter out;
    private final Observable<Response> feed;

    public StratumSocket(final PrintWriter out, final Reader input) {
        this.out = out;
        feed = observeReader(new BufferedReader(input))
                .map(new Function<String, Response>() {
                    @Override
                    public Response apply(final String json) throws Exception {
                        final ResponseDTO rdto = new Gson().fromJson(json, ResponseDTO.class);

                        return new Response(rdto.id, json);
                    }
                });
    }

    private static Observable<String> observeReader(final BufferedReader reader) {
        final ObservableOnSubscribe<String> tObservableOnSubscribe = new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) {
                try {
                    System.out.println("Creating a subscriber");
                    String line;
                    while (!subscriber.isDisposed() && (line = reader.readLine()) != null) {
                        System.out.println("Read with RX :" + line + " Thread " + Thread.currentThread().getName());
                        subscriber.onNext(line);
                    }
                } catch (final IOException e) {
                    subscriber.onError(e);
                }
                System.out.println("Completed");
                subscriber.onComplete();
            }
        };
        return Observable
                .create(tObservableOnSubscribe)
                .subscribeOn(Schedulers.io());
    }

    public void send(final Command command) {
        out.println(command);
    }

    private class ResponseDTO {
        int id;
    }

    private class Response {
        final int id;
        final String json;

        Response(final int id, final String json) {
            this.id = id;
            this.json = json;
        }
    }

    private Single<Response> responseForId(final int id) {
        return Single.fromObservable(feed.filter(new Predicate<Response>() {
            @Override
            public boolean test(final Response r) {
                return r.id == id;
            }
        }).take(1));
    }

    public Single<String> sendRx(final Command command) {
        send(command);
        return responseForId(command.getId())
                .map(new Function<Response, String>() {
                    @Override
                    public String apply(final Response response) {
                        return response.json;
                    }
                });
    }
}