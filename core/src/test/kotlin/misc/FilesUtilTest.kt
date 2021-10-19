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

import ru.endlesscode.rpginventory.FileTestBase
import ru.endlesscode.rpginventory.assertFailsWith
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystemException
import java.nio.file.NoSuchFileException
import kotlin.test.Test
import kotlin.test.assertEquals

class FilesUtilTest : FileTestBase() {

    @Test
    fun `loadFromResource - and given existing resource and - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        target.loadFromResource("/resource")

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `loadFromResource - and given existing resource without leading slash - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        target.loadFromResource("resource")

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `loadFromResource - and given existing resource and existing target file - should throw exception`() {
        // Given
        val target = createFile("existingFile")

        // Then
        assertFailsWith<FileAlreadyExistsException> {
            target.loadFromResource("/resource")
        }
    }

    @Test
    fun `loadFromResource - and given not existing resource - should throw exception`() {
        // Given
        val target = dir.resolve("newFile")

        // Then
        assertFailsWith<IllegalArgumentException>(message = "Resource file \"/notExistingResource\" not exists") {
            target.loadFromResource("/notExistingResource")
        }
    }

    @Test
    fun `readText - and given existing file - should be successful`() {
        // Given
        val text = """
            Multi-line
            existing
            file.
            С русским
            текстом.
            """.trimIndent()
        val target = createFile("existingFile", text)

        // When
        val content = target.readText()

        // Then
        assertEquals(text, content)
    }

    @Test
    fun `readText - and given not existing file - should throw exception`() {
        // Given
        val target = dir.resolve("notExistingFile")

        // Then
        assertFailsWith<NoSuchFileException> {
            target.readText()
        }
    }

    @Test
    fun `mergeFiles - and given existing directory - should merge all files`() {
        // Given
        createFile("1oneFile", "Line one")
        createFile("dir/2anotherFile", "Line two")
        createFile("dir/3thirdFile", "Line 3")

        // When
        val result = dir.mergeFiles()

        // Then
        assertFileContentEquals(result, "Line one", "Line two", "Line 3")
    }

    @Test
    fun `mergeFiles - and given existing directory and predicate - should merge only match files`() {
        // Given
        createFile("file.merge", "Line one")
        createFile("dir/fileTwo", "Skipped line")
        createFile("dir/fileThree.merge", "Line 3")

        // When
        val result = dir.mergeFiles { path -> path.toString().endsWith(".merge") }

        // Then
        assertFileContentEquals(result, "Line one", "Line 3")
    }

    @Test
    fun `mergeFiles - and given empty directory - should return empty file`() {
        // When
        val result = dir.mergeFiles()

        // Then
        assertFileContentEquals(result, emptyList())
    }

    @Test
    fun `mergeFiles - and given not a directory - should throw exception`() {
        // Given
        val file = createFile("existingFile")

        // Then
        assertFailsWith<FileSystemException> {
            file.mergeFiles()
        }
    }
}
