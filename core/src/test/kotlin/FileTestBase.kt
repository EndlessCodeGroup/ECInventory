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

package ru.endlesscode.inventory

import io.kotest.matchers.collections.shouldContainAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import ru.endlesscode.inventory.misc.listFileTree
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.AfterTest

open class FileTestBase {

    companion object {
        private lateinit var testDir: Path

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            testDir = Path("testFiles").createDirectories()
            testDir.createDirectories()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            testDir.deleteIfExists()
        }
    }

    protected val dir: Path = createTempDirectory(testDir)

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

    protected fun Path.shouldContainLines(vararg expectedContent: String) {
        readLines().shouldContainAll(*expectedContent)
    }

    protected fun deleteRecursively(path: Path) {
        path.listFileTree()
            .asReversed()
            .onEach(Path::deleteExisting)
    }
}
