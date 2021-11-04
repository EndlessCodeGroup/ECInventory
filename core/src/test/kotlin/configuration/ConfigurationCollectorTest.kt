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
import ru.endlesscode.rpginventory.item.ConfigurableItem
import ru.endlesscode.rpginventory.misc.copyTo
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigurationCollectorTest : FileTestBase() {

    private val stringValues = mapOf(
        "first" to "Nulla semper facilisis urna non fermentum.",
        "second" to "Morbi at lorem vitae odio molestie scelerisque.",
        "third" to "Vivamus non neque nec purus auctor hendrerit.",
        "fourth" to "Integer nec auctor ipsum, porttitor dictum sapien."
    )

    private val itemValues = mapOf(
        "stick" to ConfigurableItem.Builder.fromMaterial("STICK").build(),
        "magicStick" to ConfigurableItem.Builder.fromMaterial("STICK")
            .withDisplayName("&6Magic stick")
            .withLore(
                "&7This stick can be obtained in",
                "&7the &cElite dungeon&7 after defeating",
                "&7a &4Bloody swordmaster&7."
            ).build(),
        "uselessStick" to ConfigurableItem.Builder.fromMaterial("STICK")
            .withDisplayName("&7Useless stick")
            .withLore(
                "&7This stick can be obtained everywhere,",
                "&7where a tree available."
            ).build(),
        "justStick" to ConfigurableItem.Builder.fromMaterial("STICK")
            .withDisplayName("&aJust stick")
            .withLore("&7Where you found it?..")
            .build()
    )

    @Test
    fun `when collect string values - should return right values`() {
        // Given
        this.saveResource(dir, "config/stringValues.conf")
        val collector = ConfigurationCollector(dir)

        // When
        val collected = collector.collect<Map<String, String>>()

        // Then
        assertEquals(stringValues, collected)
    }

    @Test
    fun `when collect ConfigurableItemStack values - should return right values`() {
        // Given
        saveResource(dir, "config/itemValues.conf")
        val collector = ConfigurationCollector(dir)

        // When
        val collected = collector.collect<Map<String, ConfigurableItem>>()

        // Then
        assertEquals(itemValues, collected)
    }

    private fun saveResource(targetDirectory: Path, name: String) {
        val resourceAsStream = javaClass.classLoader.getResourceAsStream(name) ?: return
        val resolve = targetDirectory.resolve(name.substringAfter('/'))
        resourceAsStream.copyTo(resolve, StandardCopyOption.REPLACE_EXISTING)
    }
}