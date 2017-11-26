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

import com.google.gson.Gson;
import io.github.novacrypto.electrum.Command;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class ServerStub {

    private final ReadWriteBuffer outputBuffer = new ReadWriteBuffer().dontTerminateWhenEmpty();

    final PrintWriter input = new PrintWriter(new Writer() {

        private StringWriter stringWriter = new StringWriter();
        int processed;

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            stringWriter.write(cbuf, off, len);
            final String string = stringWriter.toString();
            int index;
            while ((index = string.indexOf('\n', processed)) != -1) {
                final String command = string.substring(processed, index);
                processed = index + 1;
                newCommand(command);
            }
        }

        @Override
        public void flush() throws IOException {
            stringWriter.flush();
        }

        @Override
        public void close() throws IOException {
            stringWriter.close();
        }
    });

    private void newCommand(String command) {
        System.out.println("ServerStub: New command: '" + command + "'");
        Command c = new Gson().fromJson(command, Command.class);
        for (CannedResponse can : cannedResponses) {
            if (can.commandPredicate.test(c)) {
                Object r = can.map.apply(c);
                System.out.println("ServerStub: Response: '" + r + "'");
                outputBuffer.printWriter.println(r);
                return;
            }
        }
    }

    final Reader output = outputBuffer.reader;
    final BufferedReader outputBufferedReader = outputBuffer.bufferedReader;

    private List<CannedResponse> cannedResponses = new ArrayList<>();

    void println(Object response) {
        outputBuffer.printWriter.println(response);
    }

    private class CannedResponse {
        final Predicate<Command> commandPredicate;
        final Function<Command, Object> map;

        CannedResponse(Predicate<Command> commandPredicate, Function<Command, Object> map) {
            this.commandPredicate = commandPredicate;
            this.map = map;
        }
    }

    ServerStub when(Predicate<Command> commandPredicate, Function<Command, Object> map) {
        cannedResponses.add(new CannedResponse(commandPredicate, map));
        return this;
    }
}