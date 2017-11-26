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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public final class StratumSocketRxReceiveTests {

    @Before
    public void setup() {
      //  RxJavaPlugins.setIoSchedulerHandler((s) -> Schedulers.trampoline());
    }

    @After
    public void teardown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void serverVersion() throws IOException, InterruptedException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        final TestObserver<String> test = stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")).test();


        Thread.sleep(1000);

        test
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    @Test
    public void serverVersion2Calls() throws IOException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")).test()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}");
        stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")).test()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }

    @Test
    public void serverVersion2CallsSomethingElsePushedOnWire() throws IOException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("server.version"),
                        c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.getId() + ", \"result\": \"ElectrumX 1.2\"}"
                );
        final StratumSocket stratumSocket = new StratumSocket(serverStub.input, serverStub.output);
        stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")).test()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}");
        serverStub.println("{\"jsonrpc\": \"2.0\", \"id\": 999, \"result\": \"ElectrumX 1.2\"}");
        stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")).test()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}");
    }
}