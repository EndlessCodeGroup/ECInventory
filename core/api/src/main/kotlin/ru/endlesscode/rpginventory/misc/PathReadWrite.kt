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
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.util.stream.Collectors
import java.util.stream.Stream

val Path.exists: Boolean get() = Files.exists(this)
val Path.notExists: Boolean get() = Files.notExists(this)

val Path.isDirectory: Boolean get() = Files.isDirectory(this)
val Path.isRegularFile: Boolean get() = Files.isRegularFile(this)

fun pathOf(path: String): Path = Paths.get(path)

fun InputStream.copyTo(target: Path, vararg options: CopyOption) {
    Files.copy(this, target, *options)
}

fun Path.moveTo(target: Path) {
    Files.move(this, target)
}

fun Path.createDirectories(): Path = Files.createDirectories(this)

fun Path.createFile(): Path = Files.createFile(this)

fun Path.delete() {
    Files.delete(this)
}

fun Path.deleteIfExists(): Boolean = Files.deleteIfExists(this)

fun Path.createTempDirectory(prefix: String? = null): Path {
    return Files.createTempDirectory(this, prefix)
}

fun Path.createTempFile(prefix: String? = null, suffix: String? = null): Path {
    return Files.createTempFile(this, prefix, suffix)
}

fun Path.readText(charset: Charset = StandardCharsets.UTF_8): String {
    return Files.lines(this, charset).collect(Collectors.joining("\n"))
}

fun Path.readAllLines(charset: Charset = StandardCharsets.UTF_8): List<String> = Files.readAllLines(this)

fun Path.writeText(text: String, charset: Charset = Charsets.UTF_8) {
    Files.write(this, text.toByteArray(charset), StandardOpenOption.CREATE)
}

fun Path.appendText(text: String, charset: Charset = Charsets.UTF_8) {
    Files.write(this, text.toByteArray(charset), StandardOpenOption.APPEND)
}

fun Path.walk(): Stream<Path> = Files.walk(this)
