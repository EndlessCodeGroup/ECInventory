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

package ru.endlesscode.rpginventory

import org.junit.AfterClass
import org.junit.BeforeClass
import ru.endlesscode.rpginventory.misc.*
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

open class FileTestBase {

    companion object {
        private lateinit var testDir: Path

        @JvmStatic
        @BeforeClass
        fun beforeAll() {
            testDir = pathOf("testFiles").createDirectories()
            testDir.createDirectories()
        }

        @JvmStatic
        @AfterClass
        fun afterAll() {
            testDir.deleteIfExists()
        }
    }

    protected lateinit var dir: Path
        private set

    @BeforeTest
    open fun setUp() {
        this.dir = testDir.createTempDirectory()
    }

    @AfterTest
    open fun tearDown() {
        deleteRecursively(dir)
    }

    protected fun createFile(path: String, content: String = ""): Path {
        val target = dir.resolve(path)
        target.parent.createDirectories()
        target.writeText(content)
        return target
    }

    protected fun assertFileContentEquals(file: Path, vararg expectedContent: String) {
        assertFileContentEquals(file, expectedContent.asList())
    }

    protected fun assertFileContentEquals(file: Path, expectedContent: List<String>) {
        assertEquals(expectedContent, file.readAllLines())
    }

    protected fun deleteRecursively(path: Path) {
        path.walk()
            .sorted(reverseOrder())
            .forEach(Path::delete)
    }
}
