/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.misc

import java.nio.file.Path
import kotlin.io.path.*

internal fun Path.loadFromResource(resource: String) {
    val validResourcePath = if (resource.startsWith("/")) resource else "/$resource"
    object {}.javaClass.getResourceAsStream(validResourcePath).use { stream ->
        requireNotNull(stream) { "Resource file \"$validResourcePath\" not exists" }
        stream.copyTo(this)
    }
}

internal fun Path.mergeFiles(predicate: (Path) -> Boolean = { true }): Path {
    val tmp: Path = createTempFile(this, suffix = ".merged")
    useFileTree {
        it.filter { path -> path.isRegularFile() && path != tmp }
            .filter(predicate)
            .map { file -> "${file.readText()}\n" }
            .forEach { content -> tmp.appendText(content) }
    }

    return tmp
}

internal fun Path.makeSureDirectoryExists() {
    if (!this.isDirectory()) {
        val tmp = parent.resolve("$fileName.niceJoke.${System.currentTimeMillis() % 10000}")
        this.moveTo(tmp)
    }

    this.createDirectories()
}
