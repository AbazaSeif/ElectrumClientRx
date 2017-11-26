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
import org.junit.ComparisonFailure;
import org.junit.Test;

public final class OutputSpyTests {

    @Test
    public void fullOutput() {
        OutputSpy outputSpy = new OutputSpy();
        outputSpy.printWriter.println("test");
        outputSpy.assertFullOutputEquals("test\n");
    }

    @Test(expected = ComparisonFailure.class)
    public void fullOutputFail() {
        OutputSpy outputSpy = new OutputSpy();
        outputSpy.printWriter.println("testA");
        outputSpy.assertFullOutputEquals("testB\n");
    }

    @Test
    public void fullOutputSingleCommand() {
        OutputSpy outputSpy = new OutputSpy();
        final Command a = Command.create(1, "a");
        outputSpy.printWriter.println(a);
        outputSpy.assertFullOutputEquals(a);
    }

    @Test(expected = ComparisonFailure.class)
    public void fullOutputSingleCommandFail() {
        OutputSpy outputSpy = new OutputSpy();
        final Command a = Command.create(1, "a");
        final Command b = Command.create(1, "b");
        outputSpy.printWriter.println(a);
        outputSpy.assertFullOutputEquals(b);
    }

    @Test
    public void fullOutputTwoCommands() {
        OutputSpy outputSpy = new OutputSpy();
        final Command a = Command.create(1, "a");
        final Command b = Command.create(2, "b");
        outputSpy.printWriter.println(a);
        outputSpy.printWriter.println(b);
        outputSpy.assertFullOutputEquals(a, b);
    }

    @Test(expected = ComparisonFailure.class)
    public void fullOutputTwoCommandsFailWrongOrder() {
        OutputSpy outputSpy = new OutputSpy();
        final Command a = Command.create(1, "a");
        final Command b = Command.create(2, "b");
        outputSpy.printWriter.println(a);
        outputSpy.printWriter.println(b);
        outputSpy.assertFullOutputEquals(b, a);
    }
}