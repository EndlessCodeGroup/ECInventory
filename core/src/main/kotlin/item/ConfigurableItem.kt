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

package ru.endlesscode.rpginventory.item

import kotlinx.serialization.Serializable

@Serializable
internal data class ConfigurableItem(
    override val material: String,
    override val damage: Int = 0,
    override val displayName: String? = null,
    override val unbreakable: Boolean = false,
    override val lore: List<String> = emptyList(),
    override val enchantments: Map<String, Int> = emptyMap(),
    override val itemFlags: List<String> = emptyList(),
) : Item {

    class Builder private constructor(material: String) {

        companion object {
            @JvmStatic
            fun fromMaterial(material: String): Builder {
                return Builder(material)
            }
        }

        var material: String = "AIR"
        var damage: Int = 0

        //Meta
        var displayName: String? = null
        var unbreakable: Boolean = false
        var lore: List<String> = emptyList()

        var enchantments: Map<String, Int> = emptyMap() // Enchantment:level
        var itemFlags: List<String> = emptyList()

        init {
            this.material = material
        }

        fun withDamage(damage: Int): Builder {
            this.damage = damage
            return this
        }

        fun withDisplayName(displayName: String): Builder {
            this.displayName = displayName
            return this
        }

        fun withLore(vararg lore: String): Builder {
            this.lore = lore.toList()
            return this
        }

        fun withLore(lore: List<String>): Builder {
            this.lore = lore.toList()
            return this
        }

        fun withItemFlags(vararg flags: String): Builder {
            this.itemFlags = flags.toList()
            return this
        }

        fun withEnchantments(enchantments: Map<String, Int>): Builder {
            this.enchantments = HashMap(enchantments)
            return this
        }

        fun unbreakable(unbreakable: Boolean): Builder {
            this.unbreakable = unbreakable
            return this
        }

        fun build(): ConfigurableItem {
            return ConfigurableItem(material, damage, displayName, unbreakable, lore, enchantments, itemFlags)
        }
    }
}
