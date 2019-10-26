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

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.Objects

@ConfigSerializable
open class ConfigurableItemStack private constructor(
    @Setting val material: String,
    @Setting val damage: Int,
    @Setting val displayName: String?,
    @Setting val unbreakable: Boolean,
    @Setting val lore: List<String>,
    @Setting val enchantments: Map<String, Int>,
    @Setting val itemFlags: List<String>
) {

    /**
     * Zero-argument constructor to be instantiated through object mapper.
     */
    @Suppress("unused")
    private constructor() : this(
        material = "AIR",
        damage = 0,
        displayName = null,
        unbreakable = false,
        lore = emptyList(),
        enchantments = emptyMap(),
        itemFlags = emptyList()
    )

    protected constructor(cis: ConfigurableItemStack)
        : this(cis.material, cis.damage, cis.displayName, cis.unbreakable, cis.lore, cis.enchantments, cis.itemFlags)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigurableItemStack) return false

        return material == other.material &&
            damage == other.damage &&
            displayName == other.displayName &&
            unbreakable == other.unbreakable &&
            lore == other.lore &&
            enchantments == other.enchantments &&
            itemFlags == other.itemFlags
    }

    override fun hashCode(): Int {
        return Objects.hash(material, damage, displayName, unbreakable, lore, enchantments, itemFlags)
    }

    override fun toString(): String {
        return "ConfigurableItemStack(material=$material, damage=$damage, displayName=$displayName)"
    }


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

        fun withLore(lore: List<String>): Builder {
            this.lore = ArrayList(lore)
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

        fun build(): ConfigurableItemStack {
            return ConfigurableItemStack(material, damage, displayName, unbreakable, lore, enchantments, itemFlags)
        }
    }
}
