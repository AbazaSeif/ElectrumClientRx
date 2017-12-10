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
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*

class ServerStub {

    private val outputBuffer = ReadWriteBuffer().dontTerminateWhenEmpty()

    private class C : Iterator<String> {

        private var processed: Int = 0
        private var nextIndex: Int = -1
        private var nextCommand: String = ""

        val stringWriter = StringWriter()

        override fun hasNext(): Boolean {
            val string = stringWriter.toString()
            nextIndex = string.indexOf('\n', processed)
            val result = nextIndex != -1
            if (result) {
                nextCommand = string.substring(processed, nextIndex)
            }
            return result
        }

        override fun next(): String {
            processed = nextIndex + 1
            return nextCommand
        }

    }

    val input = PrintWriter(object : Writer() {

        private val c = C()

        @Throws(IOException::class)
        override fun write(cbuf: CharArray, off: Int, len: Int) {
            c.stringWriter.write(cbuf, off, len)
            for (command: String in c) {
                newCommand(command)
            }
        }

        @Throws(IOException::class)
        override fun flush() {
            c.stringWriter.flush()
        }

        @Throws(IOException::class)
        override fun close() {
            c.stringWriter.close()
        }
    })

    private fun newCommand(command: String) {
        println("ServerStub: New command: '$command'")
        val c = Gson().fromJson(command, Command::class.java)
        for (can in cannedResponses) {
            if (can.commandPredicate(c)) {
                val r = can.map(c)
                println("ServerStub: Response: '$r'")
                printlnOnOutput(r)
                return
            }
        }
    }

    val output = outputBuffer.reader
    val outputBufferedReader = outputBuffer.bufferedReader

    private val cannedResponses = ArrayList<CannedResponse>()

    fun printlnOnOutput(response: Any) {
        outputBuffer.printWriter.println(response)
    }

    private class CannedResponse(
            val commandPredicate: (Command) -> Boolean,
            val map: (Command) -> Any
    )

    fun on(commandPredicate: (Command) -> Boolean, map: (Command) -> Any): ServerStub {
        cannedResponses.add(CannedResponse(commandPredicate, map))
        return this
    }
}