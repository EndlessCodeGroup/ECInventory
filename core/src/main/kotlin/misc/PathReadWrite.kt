/*
 * This file is part of RPGInventory.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.misc

import java.io.InputStream
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence
import kotlin.streams.toList

fun InputStream.copyTo(target: Path, vararg options: CopyOption): Long {
    return Files.copy(this, target, *options)
}

fun Path.listFileTree(): List<Path> = Files.walk(this).use { it.toList() }
fun <T> Path.useFileTree(block: (Sequence<Path>) -> T): T = Files.walk(this).use { block(it.asSequence()) }
