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
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class StratumSocketSendTests {

    @Test
    public void canSendCommand() {
        OutputSpy outputSpy = new OutputSpy();
        Command command = Command.create(1, "hello.world");
        new StratumSocket(outputSpy.printWriter, fakeBufferedReader()).send(command);
        outputSpy.assertFullOutputEquals(command);
    }

    private BufferedReader fakeBufferedReader() {
        return new BufferedReader(new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                return 0;
            }

            @Override
            public void close() throws IOException {

            }
        });
    }

    @Test
    public void canSendTwoCommands() {
        OutputSpy outputSpy = new OutputSpy();
        Command command0 = Command.create(0, "hello");
        Command command1 = Command.create(1, "world");
        StratumSocket stratumSocket = new StratumSocket(outputSpy.printWriter, fakeBufferedReader());
        stratumSocket.send(command0);
        stratumSocket.send(command1);
        outputSpy.assertFullOutputEquals(command0, command1);
    }
}