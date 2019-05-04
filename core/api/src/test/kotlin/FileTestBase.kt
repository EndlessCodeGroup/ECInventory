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

package ru.endlesscode.rpginventory

import org.junit.AfterClass
import org.junit.BeforeClass
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Comparator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

open class FileTestBase {

    companion object {
        private lateinit var testDir: Path

        @JvmStatic
        @BeforeClass
        fun beforeAll() {
            testDir = Files.createDirectories(Paths.get("testFiles"))
            Files.createDirectories(testDir)
        }

        @JvmStatic
        @AfterClass
        fun afterAll() {
            Files.deleteIfExists(testDir)
        }
    }

    protected lateinit var dir: Path
        private set

    @BeforeTest
    open fun setUp() {
        this.dir = Files.createTempDirectory(testDir, null)
    }

    @AfterTest
    open fun tearDown() {
        deleteRecursively(dir)
    }

    protected fun createFile(path: String, content: String = ""): Path {
        val target = dir.resolve(path)
        Files.createDirectories(target.parent)
        Files.write(target, content.toByteArray(), StandardOpenOption.CREATE)
        return target
    }

    protected fun assertFileContentEquals(file: Path, vararg expectedContent: String) {
        assertFileContentEquals(file, expectedContent.asList())
    }

    protected fun assertFileContentEquals(file: Path, expectedContent: List<String>) {
        assertEquals(expectedContent, Files.readAllLines(file))
    }

    protected fun deleteRecursively(path: Path) {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .forEach(Files::delete)
    }
}
