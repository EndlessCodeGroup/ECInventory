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

package ru.endlesscode.inventory.configuration

import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import ru.endlesscode.inventory.FileTestBase
import ru.endlesscode.inventory.configuration.data.DataConfig
import ru.endlesscode.inventory.configuration.data.InventoryConfig
import ru.endlesscode.inventory.configuration.data.SlotConfig
import ru.endlesscode.inventory.misc.copyTo
import ru.endlesscode.inventory.slot.Slot
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.test.Test

class ConfigurationCollectorTest : FileTestBase() {

    // SUT
    private val collector = ConfigurationCollector(dir)

    @Test
    fun `when collect string values - should return right values`() {
        // Given
        saveResource(dir, "config/stringValues.conf")

        // When
        val collected = collector.collect<Map<String, String>>()

        // Then
        collected shouldContainExactly mapOf(
            "first" to "Nulla semper facilisis urna non fermentum.",
            "second" to "Morbi at lorem vitae odio molestie scelerisque.",
            "third" to "Vivamus non neque nec purus auctor hendrerit.",
            "fourth" to "Integer nec auctor ipsum, porttitor dictum sapien."
        )
    }

    @Test
    fun `when collect DataConfig - should return right values`() {
        // Given
        saveResource(dir, "config/data.conf")

        // When
        val collected = collector.collect<DataConfig>()

        // Then
        collected shouldBe DataConfig(
            slots = mapOf(
                "right-ring" to SlotConfig(
                    name = "Right ring",
                    texture = "ring-slot",
                    allowedItems = listOf("minecraft:diamond_shovel", "mimic:some_texture_item"),
                    type = Slot.Type.PASSIVE,
                    maxStackSize = 1,
                ),
                "left-ring" to SlotConfig(
                    name = "Left ring",
                    texture = "ring-slot",
                    allowedItems = listOf("minecraft:diamond_shovel", "mimic:some_texture_item"),
                    type = Slot.Type.PASSIVE,
                    maxStackSize = 1,
                ),
            ),
            inventories = mapOf(
                "default" to InventoryConfig(
                    name = "ECInventory",
                    emptySlotTexture = null,
                    slots = mapOf(
                        "24" to "left-ring",
                        "26" to "right-ring",
                    ),
                ),
            )
        )
    }

    private fun saveResource(targetDirectory: Path, name: String) {
        val resourceAsStream = javaClass.classLoader.getResourceAsStream(name) ?: return
        val resolve = targetDirectory.resolve(name.substringAfter('/'))
        resourceAsStream.copyTo(resolve, StandardCopyOption.REPLACE_EXISTING)
    }
}
