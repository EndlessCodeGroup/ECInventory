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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import ru.endlesscode.rpginventory.FileTestBase
import ru.endlesscode.rpginventory.assertFailsWith
import java.nio.file.Files
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class I18NTest : FileTestBase() {

    // SUT
    private lateinit var i18n: I18N

    @BeforeTest
    override fun setUp() {
        super.setUp()

        this.i18n = spy(SimpleI18N(dir.toFile()))
    }

    @Test
    fun `create and pass directory with existing locales file - should throw exception`() {
        // Given
        val file = dir.resolve("locales")
        deleteRecursively(file)
        Files.createFile(file)

        // Then
        assertFailsWith<I18NException>(message = "Failed to create locales folder") {
            SimpleI18N(dir.toFile())
        }
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
        assertEquals("Something value", message)
        verify(i18n, never()).stripColor(any())
    }

    @Test
    fun `get message by key with strip color - should strip color`() {
        // When
        i18n.getMessage("key", true)

        // Then
        verify(i18n).stripColor(any())
    }

    @Test
    fun `get message by not existing key - should return key`() {
        // Given
        val key = "not.existing.key"

        // When
        val message = i18n.getMessage(key)

        // Then
        assertEquals(key, message)
    }

    @Test
    fun `get message with args - should return message with substituted arguments`() {
        // When
        val message = i18n.getMessage("with.args", "Text", 1)

        // Then
        assertEquals("Args: Text, 1", message)
    }

}
