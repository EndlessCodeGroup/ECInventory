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

import ru.endlesscode.rpginventory.FileTestBase
import java.nio.file.Files
import kotlin.test.*

class ConfigurationProviderTest : FileTestBase() {

    // SUT
    private lateinit var configurationProvider: ConfigurationProvider<TestConfiguration>

    @BeforeTest
    override fun setUp() {
        super.setUp()

        this.configurationProvider = ConfigurationProvider(this.dir)
    }

    @Test
    fun `when ConfigurationProvider created - configuration file should be created`() {
        // Given
        val configurationFile = this.dir.resolve("testConfiguration.conf")

        // Then
        assertTrue(Files.exists(configurationFile))
    }

    @Test
    fun `when ConfigurationProvider created - config should not be null`() {
        // When
        val config = this.configurationProvider.config

        // Then
        assertNotNull(config)
    }

    @Test
    fun `when ConfigurationProvider created - config should be loaded properly`() {
        // Given
        val local = TestConfiguration()

        // When
        val config = this.configurationProvider.config

        // Then
        assertEquals(local.aString, config.aString)
        assertEquals(local.anInt, config.anInt)
    }

    @Test
    fun `when edit configuration - and then reload it - should be loaded changed config`() {
        // Given
        val newInt = 6
        val newString = "Lorem ipsum dolor sit amet, consectetur."
        var config = this.configurationProvider.config

        // When
        config.anInt = newInt
        config.aString = newString
        this.configurationProvider.save()
        this.configurationProvider.reload()
        config = this.configurationProvider.config

        // Then
        assertEquals(newString, config.aString)
        assertEquals(newInt, config.anInt)
    }
}
