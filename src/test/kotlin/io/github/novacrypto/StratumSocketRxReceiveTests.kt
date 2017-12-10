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

package io.github.novacrypto

import io.github.novacrypto.electrum.Command
import io.github.novacrypto.electrum.StratumSocket
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test

class StratumSocketRxReceiveTests {

    @Test
    fun serverVersion() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        givenSocket(serverStub).sendRx(Command.create(456, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    private fun givenSocket(serverStub: ServerStub): StratumSocket {
        return StratumSocket(serverStub.input, serverStub.output)
    }

    @Test
    fun serverVersion2Calls() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    @Test
    fun serverVersion2CallsSomethingElsePushedOnWire() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        serverStub.printlnOnOutput("{\"jsonrpc\": \"2.0\", \"id\": 999, \"result\": \"ElectrumX 1.2\"}")
        stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    @Test
    fun serverVersion2CallsSomethingElsePushedOnWire2() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        serverStub.printlnOnOutput("{\"jsonrpc\": \"2.0\", \"id\": 4567, \"result\": \"ElectrumX 1.2\"}")
        stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10"))
                .testBlockingFirst()
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    internal inner class ServerVersion {
        var result: String? = null
    }

    @Test
    fun serverVersionTyped() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", 10)
                .testBlockingFirst()
                .assertValue { v -> v.result == "ElectrumX 1.2" }
    }

    @Test
    fun sendRxAcceptsObjects() {
        val serverStub = serverStub {
            on { c ->
                c.method == "server.version" &&
                        c.getParam(0) == "2.9.2" &&
                        (c.getParam(1) as Double).toInt() == 10
            } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", 10)
                .testBlockingFirst()
                .assertValue { v -> v.result == "ElectrumX 1.2" }
    }

    @Test
    fun idIsAutomaticallyIncremented() {
        val serverStub = serverStub {
            on { c -> c.method == "server.version" && c.id == 0 } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
            on { c -> c.method == "server.version" && c.id == 1 } returns
                    { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 2.3\"}" }
        }
        val stratumSocket = givenSocket(serverStub)
        stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", "0.10")
                .testBlockingFirst()
                .assertValue { v -> v.result == "ElectrumX 1.2" }
        stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", "0.10").
                testBlockingFirst()
                .assertValue { v -> v.result == "ElectrumX 2.3" }
    }
}

fun <T> Observable<T>.testBlockingFirst(n: Int): TestObserver<T> =
        Observable.fromIterable(
                this.take(n.toLong()).blockingIterable()
        ).test()

fun <T> Single<T>.testBlockingFirst(): TestObserver<T> = this.toObservable().testBlockingFirst(1)