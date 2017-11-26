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

public final class CommandPropertyTests {

    @Test
    public void canReadMethod_a() {
        assertEquals("a",
                Command.create(0, "a").getMethod());
    }

    @Test
    public void canReadMethod_b() {
        assertEquals("b",
                Command.create(0, "b").getMethod());
    }

    @Test
    public void canReadId0() {
        assertEquals(0,
                Command.create(0, "a").getId());
    }

    @Test
    public void canReadId1() {
        assertEquals(1,
                Command.create(1, "a").getId());
    }

    @Test
    public void canGetParamLength0() {
        assertEquals(0,
                Command.create(1, "a").getParamCount());
    }

    @Test
    public void canGetParamLength1() {
        assertEquals(1,
                Command.create(1, "a", 1).getParamCount());
    }

    @Test
    public void canGetParam1() {
        assertEquals("p1",
                Command.create(1, "a",  "p1", "p2").getParam(0));
    }

    @Test
    public void canGetParamLength2() {
        assertEquals(2,
                Command.create(1, "a", "b", "c").getParamCount());
    }

    @Test
    public void canGetParam2() {
        assertEquals("d2",
                Command.create(1, "a", "d1", "d2").getParam(1));
    }
}