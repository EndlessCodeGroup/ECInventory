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

import com.google.common.reflect.TypeParameter
import com.google.common.reflect.TypeToken
import ru.endlesscode.rpginventory.FileTestBase
import ru.endlesscode.rpginventory.item.ConfigurableItemStack
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

    private val cisValues = mapOf(
        "stick" to ConfigurableItemStack.Builder.fromMaterial("STICK").build(),
        "magicStick" to ConfigurableItemStack.Builder.fromMaterial("STICK")
            .withDisplayName("&6Magic stick")
            .withLore(
                "&7This stick can be obtained in",
                "&7the &cElite dungeon&7 after defeating",
                "&7a &4Bloody swordmaster&7."
            ).build(),
        "uselessStick" to ConfigurableItemStack.Builder.fromMaterial("STICK")
            .withDisplayName("&7Useless stick")
            .withLore(
                "&7This stick can be obtained everywhere,",
                "&7where a tree available."
            ).build(),
        "justStick" to ConfigurableItemStack.Builder.fromMaterial("STICK")
            .withDisplayName("&aJust stick")
            .withLore("&7Where you found it?..")
            .build()
    )

    @Test
    fun `when collect string values - should return right values`() {
        // Given
        this.saveResource(this.dir, "stringValues.conf")
        val collector = ConfigurationCollector(this.dir)

        // When
        val collected = collector.collect(mapToken(String::class.java, String::class.java))

        // Then
        assertEquals(this.stringValues, collected)
    }

    @Test
    fun `when collect ConfigurableItemStack values - should return right values`() {
        // Given
        this.saveResource(this.dir, "cisValues.conf")
        val collector = ConfigurationCollector(this.dir.toFile())

        // When
        val collected = collector.collect(mapToken(String::class.java, ConfigurableItemStack::class.java))

        // Then
        assertEquals(this.cisValues, collected)
    }

    private fun <K, V> mapToken(kClass: Class<K>, vClass: Class<V>): TypeToken<Map<K, V>> {
        // @formatter:off
        return object : TypeToken<Map<K, V>>() {}
            .where(object : TypeParameter<K>() {}, kClass)
            .where(object : TypeParameter<V>() {}, vClass)
        // @formatter:on
    }

    private fun saveResource(targetDirectory: Path, name: String) {
        val resourceAsStream = this.javaClass.classLoader.getResourceAsStream(name) ?: return
        val resolve = targetDirectory.resolve(name)
        resourceAsStream.copyTo(resolve, StandardCopyOption.REPLACE_EXISTING)
    }
}
