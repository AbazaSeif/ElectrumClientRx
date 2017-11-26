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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public final class StratumSocket {
    private final PrintWriter out;
    private final BufferedReader input;
    private final Observable<String> feed;
    private final CompositeDisposable rx = new CompositeDisposable();

    public StratumSocket(final PrintWriter out, final BufferedReader input) {
        this.out = out;
        this.input = input;
        final ConnectableObservable<String> from = from(input);
        feed = from;

        feed.subscribe(
                new Consumer<String>() {
                    @Override
                    public void accept(final String s) throws Exception {
                        System.out.println(s);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable s) throws Exception {
                        System.out.println(s);
                    }
                }
        );

        rx.add(from.connect());
    }

    public static ConnectableObservable<String> from(final BufferedReader reader) {
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
                .subscribeOn(Schedulers.io())
                .publish();
    }


    public void send(final Command command) {
        out.println(command);
    }

    public Single<String> sendRx(final Command command) throws IOException {
        send(command);
        return Single.fromObservable(feed.take(1))
                .doOnSuccess(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("s: " + s);
                    }
                });
    }

    public <Result> Single<Result> sendRx(final Command command, final Class<Result> clazz) throws IOException {
        return sendRx(command)
                .map(new Function<String, Result>() {
                    @Override
                    public Result apply(final String r) throws Exception {
                        return new Gson().fromJson(r, clazz);
                    }
                });
    }
}
