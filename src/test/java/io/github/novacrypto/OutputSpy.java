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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

class OutputSpy {
    final StringWriter out = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(out);

    void assertFullOutputEquals(String expected) {
        assertEquals(expected, out.toString());
    }

    void assertFullOutputEquals(Command command) {
        assertFullOutputEquals(command + "\n");
    }

    void assertFullOutputEquals(Command... commands) {
        StringBuilder sb = new StringBuilder();
        for (Command c : commands) {
            sb.append(c);
            sb.append('\n');
        }
        assertFullOutputEquals(sb.toString());
    }
}