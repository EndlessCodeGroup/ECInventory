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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Comparator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

open class FileTestBase {
    protected lateinit var testDir: Path
    protected lateinit var tmpDir: Path

    @BeforeTest
    open fun setUp() {
        this.testDir = Files.createDirectories(Paths.get("testFiles"))
        this.tmpDir = Files.createTempDirectory(testDir, null)
    }

    @AfterTest
    open fun tearDown() {
        tmpDir.asSequence()
        Files.walk(tmpDir)
            .sorted(Comparator.reverseOrder())
            .forEach(Files::delete)
    }

    protected fun createFile(path: String, content: String) {
        val target = tmpDir.resolve(path)
        Files.createDirectories(target.parent)
        Files.write(target, content.toByteArray(), StandardOpenOption.CREATE)
    }

    protected fun assertFileContentEquals(file: Path, vararg content: String) {
        assertFileContentEquals(file, content.asList())
    }

    protected fun assertFileContentEquals(file: Path, content: List<String>) {
        val strings = Files.readAllLines(file)
        assertEquals(content, strings)
    }
}
