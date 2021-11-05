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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.spyk
import io.mockk.verify
import ru.endlesscode.rpginventory.FileTestBase
import kotlin.io.path.createFile
import kotlin.test.Test

class I18NTest : FileTestBase() {

    // SUT
    private val i18n: I18N = spyk(SimpleI18N(dir.toFile()))

    @Test
    fun `create and pass directory with existing locales file - should throw exception`() {
        // Given
        val file = dir.resolve("locales")
        deleteRecursively(file)
        file.createFile()

        // Then
        shouldThrow<I18NException> { SimpleI18N(dir.toFile()) }
            .shouldHaveMessage("Failed to create locales folder")
    }

    @Test
    fun `reload existing locale - should be successful`() {
        // When
        i18n.reload("test")
    }

    @Test
    fun `reload existing locale with changed case - should be successful`() {
        // When
        i18n.reload("TeSt")
    }

    @Test
    fun `get message by key - should return right message`() {
        // When
        val message = i18n.getMessage("key")

        // Then
        message shouldBe "Something value"
        verify(exactly = 0) { i18n.stripColor(any()) }
    }

    @Test
    fun `get message by key with strip color - should strip color`() {
        // When
        i18n.getMessage("key", true)

        // Then
        verify { i18n.stripColor(any()) }
    }

    @Test
    fun `get message by not existing key - should return key`() {
        // Given
        val key = "not.existing.key"

        // When
        val message = i18n.getMessage(key)

        // Then
        message shouldBe key
    }

    @Test
    fun `get message with args - should return message with substituted arguments`() {
        // When
        val message = i18n.getMessage("with.args", "Text", 1)

        // Then
        message shouldBe "Args: Text, 1"
    }

}
