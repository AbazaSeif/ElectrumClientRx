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

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

class ReadWriteBuffer {
    private final AtomicInteger end = new AtomicInteger(-1);

    private StringBuffer shared = new StringBuffer();

    final BufferedReader bufferedReader;
    final PrintWriter printWriter;

    ReadWriteBuffer() {
        printWriter = new PrintWriter(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                shared.append(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        });
        bufferedReader = new BufferedReader(new Reader() {
            int pos;

            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                if (pos >= shared.length()) {
                    return end.get();
                }
                int end = Math.min(pos + len, shared.length());
                shared.getChars(pos, end, cbuf, off);
                len = end - pos;
                pos += len;
                return len;
            }

            @Override
            public void close() throws IOException {
            }
        });
    }

    public ReadWriteBuffer dontTerminateWhenEmpty() {
        end.set(0);
        return this;
    }

    public ReadWriteBuffer terminateWhenEmpty() {
        end.set(-1);
        return this;
    }
}