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

import com.google.gson.Gson;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Command {
    private final int id;
    private final String method;
    private final Object[] params;

    private Command(final int id, final String method, final Object[] params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public static Command create(
            final int id,
            final String method,
            final Object... params
    ) {
        return new Command(id, method, params);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}