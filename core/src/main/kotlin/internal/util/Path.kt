/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2021 EndlessCode Group and contributors
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
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.moveTo
import kotlin.streams.asSequence
import kotlin.streams.toList

internal fun Path.loadFromResource(resource: String) {
    val validResourcePath = if (resource.startsWith("/")) resource else "/$resource"
    object {}.javaClass.getResourceAsStream(validResourcePath).use { stream ->
        requireNotNull(stream) { "Resource file \"$validResourcePath\" not exists" }
        stream.copyTo(this)
    }
}

internal fun Path.makeSureDirectoryExists() {
    if (this.isRegularFile()) {
        val tmp = parent.resolve("$fileName.niceJoke.${System.currentTimeMillis() % 10000}")
        this.moveTo(tmp)
    }

    this.createDirectories()
}

internal fun InputStream.copyTo(target: Path, vararg options: CopyOption): Long {
    return Files.copy(this, target, *options)
}

internal fun Path.listFileTree(): List<Path> = Files.walk(this).use { it.toList() }
internal fun <T> Path.useFileTree(block: (Sequence<Path>) -> T): T = Files.walk(this).use { block(it.asSequence()) }
