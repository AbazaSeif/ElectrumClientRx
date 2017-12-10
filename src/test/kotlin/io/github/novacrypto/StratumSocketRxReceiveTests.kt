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

import java.io.IOException

class StratumSocketRxReceiveTests {

    @Test
    fun serverVersion() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)

        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    private fun givenSocket(serverStub: ServerStub): StratumSocket {
        return StratumSocket(serverStub.input, serverStub.output)
    }

    @Test
    fun serverVersion2Calls() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    @Test
    fun serverVersion2CallsSomethingElsePushedOnWire() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        serverStub.printlnOnOutput("{\"jsonrpc\": \"2.0\", \"id\": 999, \"result\": \"ElectrumX 1.2\"}")
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    @Test
    fun serverVersion2CallsSomethingElsePushedOnWire2() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(Command.create(123, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 123, \"result\": \"ElectrumX 1.2\"}")
        serverStub.printlnOnOutput("{\"jsonrpc\": \"2.0\", \"id\": 4567, \"result\": \"ElectrumX 1.2\"}")
        testBlockingFirst(stratumSocket.sendRx(Command.create(456, "server.version", "2.9.2", "0.10")))
                .assertValue("{\"jsonrpc\": \"2.0\", \"id\": 456, \"result\": \"ElectrumX 1.2\"}")
    }

    internal inner class ServerVersion {
        var result: String? = null
    }

    @Test
    fun serverVersionTyped() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", 10))
                .assertValue { v -> v.result == "ElectrumX 1.2" }
    }

    @Test
    fun sendRxAcceptsObjects() {
        val serverStub = ServerStub()
                .`when`(
                        { c ->
                            c.method == "server.version" &&
                                    c.getParam(0) == "2.9.2" &&
                                    (c.getParam(1) as Double).toInt() == 10
                        }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", 10))
                .assertValue { v -> v.result == "ElectrumX 1.2" }
    }

    @Test
    fun idIsAutomaticallyIncremented() {
        val serverStub = ServerStub()
                .`when`(
                        { c -> c.method == "server.version" && c.id == 0 }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 1.2\"}" }
                .`when`(
                        { c -> c.method == "server.version" && c.id == 1 }
                ) { c -> "{\"jsonrpc\": \"2.0\", \"id\": " + c.id + ", \"result\": \"ElectrumX 2.3\"}" }
        val stratumSocket = givenSocket(serverStub)
        testBlockingFirst(stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", "0.10"))
                .assertValue { v -> v.result == "ElectrumX 1.2" }
        testBlockingFirst(stratumSocket.sendRx(ServerVersion::class.java, "server.version", "2.9.2", "0.10"))
                .assertValue { v -> v.result == "ElectrumX 2.3" }
    }

    companion object {

        internal fun <T> testBlockingFirst(stringObservable: Observable<T>, n: Int): TestObserver<T> {
            return Observable.fromIterable(
                    stringObservable
                            .take(n.toLong())
                            .blockingIterable()
            ).test()
        }

        internal fun <T> testBlockingFirst(stringSingle: Single<T>): TestObserver<T> {
            return testBlockingFirst(stringSingle.toObservable(), 1)
        }
    }
}