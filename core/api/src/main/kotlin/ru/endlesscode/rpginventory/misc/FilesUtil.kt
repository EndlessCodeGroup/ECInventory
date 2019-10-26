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

import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors

object FilesUtil {

    @JvmStatic
    @JvmOverloads
    fun readFileToString(file: Path, charset: Charset = StandardCharsets.UTF_8): String {
        try {
            return Files.lines(file, charset).collect(Collectors.joining("\n"))
        } catch (e: IOException) {
            throw IllegalArgumentException("Given file \"${file.toAbsolutePath()}\" can't be read", e)
        }
    }

    @JvmStatic
    fun copyResourceToFile(resource: String, file: Path) {
        val validResourcePath = if (resource.startsWith("/")) resource else "/$resource"

        try {
            FilesUtil::class.java.getResourceAsStream(validResourcePath).use { stream ->
                requireNotNull(stream) { "Resource file \"$validResourcePath\" not exists" }
                Files.copy(stream, file)
            }
        } catch (e: IOException) {
            throw IllegalArgumentException("Failed to copy \"$validResourcePath\" to given target: \"${file.toAbsolutePath()}\"", e)
        }

    }

    @JvmStatic
    @JvmOverloads
    fun mergeFiles(pathToDir: Path, predicate: (Path) -> Boolean = { true }): Path {
        val tmp: Path
        try {
            tmp = Files.createTempFile(pathToDir, null, ".merged")
            Files.walk(pathToDir)
                .filter { path -> Files.isRegularFile(path) && path != tmp }
                .filter(predicate)
                .map { file -> "${readFileToString(file)}\n" }
                .forEach { content -> Files.write(tmp, content.toByteArray(), StandardOpenOption.APPEND) }
        } catch (e: IOException) {
            throw IllegalArgumentException("Files in given directory \"${pathToDir.toAbsolutePath()}\" can't be merged", e)
        }

        return tmp
    }

    @JvmStatic
    @Throws(IOException::class)
    fun makeSureDirectoryExists(directory: Path) {
        if (!Files.isDirectory(directory)) {
            val tmp = directory.parent
                .resolve("${directory.fileName}.niceJoke.${System.currentTimeMillis() % 10000}")
            Files.move(directory, tmp)
        }

        Files.createDirectories(directory)
    }

}
