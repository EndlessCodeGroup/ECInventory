/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
 *
 * ECInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ECInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ECInventory. If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.inventory.internal.util

import java.util.logging.Level
import java.util.logging.Logger

internal object Log {

    private var logger: Logger? = null

    /** Initializes Log with the given [logger]. */
    fun init(logger: Logger) {
        Log.logger = logger
    }

    /** Writes info message to log. */
    fun i(message: String) {
        logger?.info(message)
    }

    /** Writes warning message to log. */
    fun w(vararg messages: String) {
        messages.forEach { logger?.warning(it) }
    }

    /** Writes error message to log. */
    fun e(message: String, throwable: Throwable) {
        logger?.log(Level.SEVERE, message, throwable)
    }
}
