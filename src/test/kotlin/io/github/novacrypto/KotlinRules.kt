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

package io.github.novacrypto

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private class Rule(private val before: () -> Unit, private val after: () -> Unit) : TestRule {
    override fun apply(base: Statement, description: Description?) =
            object : Statement() {
                override fun evaluate() {
                    try {
                        before()
                        base.evaluate()
                    } finally {
                        after()
                    }
                }
            }
}

interface After {
    infix fun after(doThis: () -> Unit): TestRule
}

private class Before(private val doThat: () -> Unit) : After {
    override infix fun after(doThis: () -> Unit) = Rule(doThat, doThis)
}

fun before(doThis: () -> Unit): After = Before(doThis)