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

import com.google.gson.Gson
import io.github.novacrypto.electrum.Command
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerStubTests {

    @Suppress("unused")
    internal inner class Response(private val id: Int, private val result: String) {

        override fun toString(): String {
            return Gson().toJson(this)
        }
    }

    @Test
    fun `can setup a response`() {
        val serverStub = serverStub {
            on { c -> c.method == "a" } returns
                    { c -> Response(c.id, "a.response") }
        }
        serverStub.input.println(Command.create(123, "a"))
        assertEquals(Response(123, "a.response").toString(), serverStub.outputBufferedReader.readLine())
    }

    @Test
    fun `can setup a response with String direct`() {
        val serverStub = serverStub {
            on { c -> c.method == "a" } returns
                    { "{\"id\":1,\"method\":\"blockchain.numblocks.subscribe\",\"params\":[]}" }
        }
        serverStub.input.println(Command.create(123, "a"))
        assertEquals("{\"id\":1,\"method\":\"blockchain.numblocks.subscribe\",\"params\":[]}",
                serverStub.outputBufferedReader.readLine())
    }

    @Test
    fun `can setup two responses`() {
        val serverStub = serverStub {
            on { c -> c.id == 1 } returns
                    { c -> Response(c.id, "x") }
            on { c -> c.id == 2 } returns
                    { c -> Response(c.id, "y") }
        }
        serverStub.input.println(Command.create(2, "a"))
        serverStub.input.println(Command.create(1, "a"))
        assertEquals(Response(2, "y").toString(), serverStub.outputBufferedReader.readLine())
        assertEquals(Response(1, "x").toString(), serverStub.outputBufferedReader.readLine())
    }

    @Test
    fun `can put something on wire directly`() {
        val serverStub = ServerStub()
        serverStub.printlnOnOutput(Response(123, "a.response"))
        assertEquals(Response(123, "a.response").toString(), serverStub.outputBufferedReader.readLine())
    }
}