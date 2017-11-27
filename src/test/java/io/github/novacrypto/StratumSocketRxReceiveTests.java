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

package io.github.novacrypto;

import io.github.novacrypto.electrum.Command;
import io.github.novacrypto.electrum.StratumSocket;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import java.io.IOException;

public final class StratumSocketRxReceiveTests {

    @Test
    public void serverVersion() throws IOException, InterruptedException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);

        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    static <T> TestObserver<T> testBlockingFirst(Observable<T> stringObservable, int n) {
        return Observable.fromIterable(
                stringObservable
                        .take(n)
                        .blockingIterable()
        ).test();
    }

    static <T> TestObserver<T> testBlockingFirst(Single<T> stringSingle) {
        return testBlockingFirst(stringSingle.toObservable(), 1);
    }

    @Test
    public void serverVersion2Calls() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}");
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    @Test
    public void serverVersion2CallsSomethingElsePushedOnWire() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}");
        serverStub.println("{\"jsonrpc\": \"2.0\", \"id\": 999, \"result\": \"ElectrumX 1.2\"}");
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    @Test
    public void serverVersion2CallsSomethingElsePushedOnWire2() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}");
        serverStub.println("{\"jsonrpc\": \"2.0\", \"id\": 4567, \"result\": \"ElectrumX 1.2\"}");
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    class ServerVersion {
        String result;
    }

    @Test
    public void serverVersionTyped() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(ServerVersion.class, "server.version", "2.9.2", 10))
                .assertValue(v -> v.result.equals("ElectrumX 1.2"));
    }

    @Test
    public void sendRxAcceptsObjects() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version") &&
                                c.getParam(0).equals("2.9.2") &&
                                ((Double) c.getParam(1)).intValue() == 10,
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(ServerVersion.class, "server.version", "2.9.2", 10))
                .assertValue(v -> v.result.equals("ElectrumX 1.2"));
    }

    @Test
    public void idIsAutomaticallyIncremented() {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version") && c.getId() == 0,
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                )
                .when(
                        c -> c.getMethod().equals("server.version") && c.getId() == 1,
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 2.3\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        testBlockingFirst(stratumSocket.sendRx(ServerVersion.class, "server.version", "2.9.2", "0.10"))
                .assertValue(v -> v.result.equals("ElectrumX 1.2"));
        testBlockingFirst(stratumSocket.sendRx(ServerVersion.class, "server.version", "2.9.2", "0.10"))
                .assertValue(v -> v.result.equals("ElectrumX 2.3"));
    }
}