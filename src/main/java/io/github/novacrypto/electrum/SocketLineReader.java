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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SocketLineReader implements LineReader {

    private final Socket socket;
    private final AtomicBoolean closing = new AtomicBoolean();
    private final BufferedReader reader;

    public SocketLineReader(final Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public String readLine() throws IOException {
        try {
            return reader.readLine();
        } catch (final IOException e) {
            if (closing.get()) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        closing.set(true);
        socket.close();
    }
}
