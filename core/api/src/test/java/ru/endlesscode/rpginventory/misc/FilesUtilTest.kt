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
    fun copyResourceToFile_existingResourceToNewFileMustBeSuccessful() {
        // Given
        val target = tmpDir.resolve("resource")

        // When
        FilesUtil.copyResourceToFile("/resource", target)

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun copyResourceToFile_resourceWithoutStartingSlashMustBeSuccessful() {
        // Given
        val target = tmpDir.resolve("resource")

        // When
        FilesUtil.copyResourceToFile("resource", tmpDir.resolve("resource"))

        // Then
        assertFileContentEquals(target, "This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun copyResourceToFile_existingResourceToExistingFileMustThrowException() {
        // Given
        val target = testDir.resolve("existingFile")

        try {
            // When
            FilesUtil.copyResourceToFile("/resource", target)
        } catch (e: IllegalArgumentException) {
            // Then
            val expectedMessage = String.format(
                "Failed to copy \"/resource\" to given target: \"%s\"",
                target.toAbsolutePath().toString()
            )
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<FileAlreadyExistsException>(e.cause)
            return
        }

        fail()
    }

    @Test
    fun copyResourceToFile_notExistingResourceToNewFileMustThrowException() {
        // Given
        val target = tmpDir.resolve("newFile")

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
    fun readFileToString_existingFileMustBeSuccessful() {
        // Given
        val target = testDir.resolve("existingFile")

        // When
        val content = FilesUtil.readFileToString(target)

        // Then
        assertEquals("Multi-line\nexisting\nfile.\nС русским\nтекстом.", content)
    }

    @Test
    fun readFileToString_notExistingFileMustThrowException() {
        // Given
        val target = testDir.resolve("notExistingFile")

        try {
            // When
            FilesUtil.readFileToString(target)
        } catch (e: IllegalArgumentException) {
            // Then
            val expectedMessage = String.format(
                "Given file \"%s\" can't be read",
                target.toAbsolutePath().toString()
            )
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<NoSuchFileException>(e.cause)
            return
        }

        fail()
    }

    @Test
    fun mergeFiles_existingDirectoryShouldBeSuccessful() {
        // Given
        createFile("1oneFile", "Line one")
        createFile("dir/2anotherFile", "Line two")
        createFile("dir/3thirdFile", "Line 3")

        // When
        val result = FilesUtil.mergeFiles(tmpDir)

        // Then
        assertFileContentEquals(result, "Line one", "Line two", "Line 3")
    }

    @Test
    fun mergeFiles_withPredicateShouldMergeOnlyMatchFiles() {
        // Given
        createFile("file.merge", "Line one")
        createFile("dir/fileTwo", "Skipped line")
        createFile("dir/fileThree.merge", "Line 3")

        // When
        val result = FilesUtil.mergeFiles(tmpDir) { path -> path.toString().endsWith(".merge") }

        // Then
        assertFileContentEquals(result, "Line one", "Line 3")
    }

    @Test
    fun mergeFiles_emptyDirectoryShouldReturnEmptyFile() {
        // When
        val result = FilesUtil.mergeFiles(tmpDir)

        // Then
        assertFileContentEquals(result!!, emptyList())
    }

    @Test
    fun mergeFiles_notDirectoryShouldThrowException() {
        // Given
        val file = testDir.resolve("existingFile")

        try {
            // When
            FilesUtil.mergeFiles(file)
        } catch (e: Exception) {
            // Then
            val expectedMessage = String.format(
                "Files in given directory \"%s\" can't be merged",
                file.toAbsolutePath().toString()
            )
            assertEquals(expectedMessage, e.message)
            assertInstanceOf<FileSystemException>(e.cause)
            return
        }

        fail()
    }
}
