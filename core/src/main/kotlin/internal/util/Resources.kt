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

import java.io.InputStream
import java.nio.file.Path

internal fun Path.loadFromResource(resource: String) {
    val validResourcePath = if (resource.startsWith("/")) resource else "/$resource"
    useResourceStream(validResourcePath) {
        it.copyTo(this)
    }
}

internal fun <T> useResourceStream(path: String, block: (InputStream) -> T): T {
    return object {}.javaClass.getResourceAsStream(path).use { stream ->
        requireNotNull(stream) { "Resource file \"$path\" not exists" }
        block(stream)
    }
}
