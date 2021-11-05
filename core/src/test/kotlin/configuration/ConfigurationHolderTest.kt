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

package ru.endlesscode.rpginventory.configuration

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import ru.endlesscode.rpginventory.FileTestBase
import kotlin.test.Test

class ConfigurationHolderTest : FileTestBase() {

    // SUT
    private val configurationHolder = ConfigurationHolder(dir, TestConfiguration.SERIALIZER)
    private val config get() = configurationHolder.config

    @Test
    fun `when ConfigurationProvider created - configuration file should be created`() {
        // Given
        val configurationFile = dir.resolve("test.conf")

        // Then
        configurationFile.shouldExist()
    }

    @Test
    fun `when ConfigurationProvider created - config should not be null`() {
        // When
        val config = configurationHolder.config

        // Then
        config.shouldNotBeNull()
    }

    @Test
    fun `when ConfigurationProvider created - config should be loaded properly`() {
        // Given
        val local = TestConfiguration()

        // Then
        assertSoftly {
            config.aString shouldBe local.aString
            config.anInt shouldBe local.anInt
        }
    }

    @Test
    fun `when edit configuration - and then reload it - should be loaded changed config`() {
        // Given
        val newInt = 6
        val newString = "Lorem ipsum dolor sit amet, consectetur."

        // When
        config.anInt = newInt
        config.aString = newString
        configurationHolder.save()
        configurationHolder.reload()

        // Then
        assertSoftly {
            config.aString shouldBe newString
            config.anInt shouldBe newInt
        }
    }
}
