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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public final class StratumSocket {
    private final PrintWriter out;
    private final Observable<String> feed;

    public StratumSocket(final PrintWriter out, final Reader input) {
        this.out = out;
        feed = observeReader(new BufferedReader(input));
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

    public Single<String> sendRx(final Command command) throws IOException {
        send(command);
        return Single.fromObservable(feed.filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.contains("" + command.getId());
            }
        }).take(1));
    }

//    public <Result> Single<Result> sendRx(final Command command, final Class<Result> clazz) throws IOException {
//        return sendRx(command)
//                .map(new Function<String, Result>() {
//                    @Override
//                    public Result apply(final String r) throws Exception {
//                        return new Gson().fromJson(r, clazz);
//                    }
//                });
//    }
}
