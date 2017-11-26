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
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public final class ServerStubTests {

    class Response {

        private final int id;
        private final String result;

        Response(int id, String result) {
            this.id = id;
            this.result = result;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    @Test
    public void canSetupAResponse() throws IOException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("a"),
                        c -> new Response(c.getId(), "a.response")
                );
        serverStub.input.println(Command.create(123, "a"));
        assertEquals(new Response(123, "a.response").toString(), serverStub.output.readLine());
    }

    @Test
    public void canSetupAResponseWithStringDirect() throws IOException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getMethod().equals("a"),
                        c -> "{\"id\":1,\"method\":\"blockchain.numblocks.subscribe\",\"params\":[]}"
                );
        serverStub.input.println(Command.create(123, "a"));
        assertEquals("{\"id\":1,\"method\":\"blockchain.numblocks.subscribe\",\"params\":[]}", serverStub.output.readLine());
    }

    @Test
    public void canSetupTwoResponses() throws IOException {
        final ServerStub serverStub = new ServerStub()
                .when(
                        c -> c.getId() == 1,
                        c -> new Response(c.getId(), "x")
                ).when(
                        c -> c.getId() == 2,
                        c -> new Response(c.getId(), "y")
                );
        serverStub.input.println(Command.create(2, "a"));
        serverStub.input.println(Command.create(1, "a"));
        assertEquals(new Response(2, "y").toString(), serverStub.output.readLine());
        assertEquals(new Response(1, "x").toString(), serverStub.output.readLine());
    }
}