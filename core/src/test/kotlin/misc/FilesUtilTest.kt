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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import ru.endlesscode.rpginventory.FileTestBase
import java.nio.file.FileAlreadyExistsException
import kotlin.test.Test

class FilesUtilTest : FileTestBase() {

    @Test
    fun `loadFromResource - and given existing resource and - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        target.loadFromResource("/resource")

        // Then
        target.shouldContainLines("This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `loadFromResource - and given existing resource without leading slash - should be successful`() {
        // Given
        val target = dir.resolve("resource")

        // When
        target.loadFromResource("resource")

        // Then
        target.shouldContainLines("This is a test resource file.", "Это тестовый файл ресурсов.")
    }

    @Test
    fun `loadFromResource - and given existing resource and existing target file - should throw exception`() {
        // Given
        val target = createFile("existingFile")

        // Then
        shouldThrow<FileAlreadyExistsException> {
            target.loadFromResource("/resource")
        }
    }

    @Test
    fun `loadFromResource - and given not existing resource - should throw exception`() {
        // Given
        val target = dir.resolve("newFile")

        // Then
        shouldThrow<IllegalArgumentException> {
            target.loadFromResource("/notExistingResource")
        }.shouldHaveMessage("Resource file \"/notExistingResource\" not exists")
    }
}
