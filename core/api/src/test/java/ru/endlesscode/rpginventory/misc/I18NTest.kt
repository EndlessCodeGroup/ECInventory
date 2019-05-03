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

import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import ru.endlesscode.rpginventory.FileTestBase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail


class I18NTest : FileTestBase() {

    // SUT
    private lateinit var i18n: I18N

    @BeforeTest
    override fun setUp() {
        super.setUp()

        this.i18n = Mockito.spy(SimpleI18N(tmpDir.toFile()))
    }

    @Test
    fun constructor_creatingDirectoryWithExistingFileMustThrowException() {
        try {
            // When
            SimpleI18N(testDir.toFile())
        } catch (e: I18NException) {
            // Then
            assertEquals("Failed to create locales folder", e.message)
            return
        }

        fail()
    }

    @Test
    fun reload_reloadingExistingLocaleMustBeSuccessful() {
        // When
        i18n.reload("test")
    }

    @Test
    fun reload_reloadingMustBeCaseInsensitive() {
        // When
        i18n.reload("TeSt")
    }

    @Test
    fun getMessage_byKey() {
        // When
        val message = i18n.getMessage("key")

        // Then
        assertEquals("Something value", message)
        Mockito.verify<I18N>(i18n, Mockito.never()).stripColor(ArgumentMatchers.anyString())
    }

    @Test
    fun getMessage_byKeyWithStripColor() {
        // When
        i18n.getMessage("key", true)

        // Then
        Mockito.verify<I18N>(i18n).stripColor(ArgumentMatchers.anyString())
    }

    @Test
    fun getMessage_notExistingKeyMustReturnKey() {
        // Given
        val key = "not.existing.key"

        // When
        val message = i18n.getMessage(key)

        // Then
        assertEquals(key, message)
    }

    @Test
    fun getMessage_byKeyWithArgs() {
        // When
        val message = i18n.getMessage("with.args", "Text", 1)

        // Then
        assertEquals("Args: Text, 1", message)
    }

}
