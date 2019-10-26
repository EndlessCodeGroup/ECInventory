/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory

import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

inline fun <reified T> assertInstanceOf(value: Any?, message: String? = null) {
    assertInstanceOf(T::class.java, value, message)
}

fun assertInstanceOf(aClass: Class<*>, value: Any?, message: String? = null) {
    assertThat(message, value, instanceOf(aClass))
}

inline fun <reified T : Throwable> assertFailsWith(
    message: String? = null,
    cause: KClass<out Throwable>? = null,
    noinline block: () -> Unit
) {
    try {
        block.invoke()
    } catch (e: Throwable) {
        if (e !is T) throw e
        message?.let { assertEquals(message, e.message, "Wrong exception message.") }
        cause?.let { assertInstanceOf(cause.java, e.cause, "Wrong exception cause.") }
        return
    }

    fail("Exception ${T::class.java} expected.")
}
