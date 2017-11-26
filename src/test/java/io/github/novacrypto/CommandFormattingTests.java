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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class CommandFormattingTests {

    @Test
    public void canFormatCommandZeroArgs() {
        assertEquals("{\"id\":0,\"method\":\"a\",\"params\":[]}",
                Command.create(0, "a").toString());
    }

    @Test
    public void canFormatCommandOneArgs() {
        assertEquals("{\"id\":0,\"method\":\"a\",\"params\":[\"b\"]}",
                Command.create(0, "a", "b").toString());
    }

    @Test
    public void canFormatCommandTwoArgs() {
        assertEquals("{\"id\":0,\"method\":\"a\",\"params\":[\"b\",\"c\"]}",
                Command.create(0, "a", "b", "c").toString());
    }

    @Test
    public void canFormatCommandOneIntegerArgs() {
        assertEquals("{\"id\":0,\"method\":\"a\",\"params\":[123,\"c\"]}",
                Command.create(0, "a", 123, "c").toString());
    }

    @Test
    public void canFormatCommandDifferentId() {
        assertEquals("{\"id\":1,\"method\":\"a\",\"params\":[]}",
                Command.create(1, "a").toString());
    }

    @Test
    public void canFormatServerVersionCommand() {
        assertEquals("{\"id\":12345,\"method\":\"server.version\",\"params\":[\"2.9.2\",\"0.10\"]}",
                Command.create(12345, "server.version", "2.9.2", "0.10").toString());
    }
}