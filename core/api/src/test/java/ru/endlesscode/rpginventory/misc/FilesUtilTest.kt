/*
 * This file is part of RPGInventory.
 * Copyright (C) 2017 EndlessCode Group and contributors
 *
 * RPGInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPGInventory.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.misc

import ru.endlesscode.rpginventory.FileTestBase
import ru.endlesscode.rpginventory.assertInstanceOf
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystemException
import java.nio.file.NoSuchFileException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class FilesUtilTest : FileTestBase() {

    @Test
    fun `copyResourceToFile - and given existing resource and - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        FilesUtil.copyResourceToFile("/resource", target)

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `copyResourceToFile - and given existing resource without leading slash - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        FilesUtil.copyResourceToFile("resource", dir.resolve("resource"))

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `copyResourceToFile - and given existing resource and existing target file - should throw exception`() {
        // Given
        val target = createFile("existingFile")

        try {
            // When
            FilesUtil.copyResourceToFile("/resource", target)
        } catch (e: IllegalArgumentException) {
            // Then
            val expectedMessage = "Failed to copy \"/resource\" to given target: \"${target.toAbsolutePath()}\""
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<FileAlreadyExistsException>(e.cause)
            return
        }

        fail()
    }

    @Test
    fun `copyResourceToFile - and given not existing resource - should throw exception`() {
        // Given
        val target = dir.resolve("newFile")

        try {
            // When
            FilesUtil.copyResourceToFile("/notExistingResource", target)
        } catch (e: IllegalArgumentException) {
            // Then
            assertEquals("Resource file \"/notExistingResource\" not exists", e.message)
            assertNull(e.cause)
            return
        }

        fail()
    }

    @Test
    fun `readFileToString - and given existing file - should be successful`() {
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
        val content = FilesUtil.readFileToString(target)

        // Then
        assertEquals(text, content)
    }

    @Test
    fun `readFileToString - and given not existing file - should throw exception`() {
        // Given
        val target = dir.resolve("notExistingFile")

        try {
            // When
            FilesUtil.readFileToString(target)
        } catch (e: IllegalArgumentException) {
            // Then
            val expectedMessage = "Given file \"${target.toAbsolutePath()}\" can't be read"
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<NoSuchFileException>(e.cause)
            return
        }

        fail()
    }

    @Test
    fun `mergeFiles - and given existing directory - should merge all files`() {
        // Given
        createFile("1oneFile", "Line one")
        createFile("dir/2anotherFile", "Line two")
        createFile("dir/3thirdFile", "Line 3")

        // When
        val result = FilesUtil.mergeFiles(dir)

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
        val result = FilesUtil.mergeFiles(dir) { path -> path.toString().endsWith(".merge") }

        // Then
        assertFileContentEquals(result, "Line one", "Line 3")
    }

    @Test
    fun `mergeFiles - and given empty directory - should return empty file`() {
        // When
        val result = FilesUtil.mergeFiles(dir)

        // Then
        assertFileContentEquals(result!!, emptyList())
    }

    @Test
    fun `mergeFiles - and given not a directory - should throw exception`() {
        // Given
        val file = createFile("existingFile")

        try {
            // When
            FilesUtil.mergeFiles(file)
        } catch (e: Exception) {
            // Then
            val expectedMessage = "Files in given directory \"${file.toAbsolutePath()}\" can't be merged"
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<FileSystemException>(e.cause)
            return
        }

        fail()
    }
}
