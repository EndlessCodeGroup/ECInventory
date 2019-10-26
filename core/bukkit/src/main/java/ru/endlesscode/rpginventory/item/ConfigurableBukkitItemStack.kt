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

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import ru.endlesscode.rpginventory.util.safeValueOf
import ru.endlesscode.rpginventory.util.translateColorCodes

class ConfigurableBukkitItemStack private constructor(cis: ConfigurableItemStack) : ConfigurableItemStack(cis) {

    companion object {
        fun from(cis: ConfigurableItemStack): ConfigurableBukkitItemStack {
            return ConfigurableBukkitItemStack(cis)
        }
    }

    fun toItemStack(): ItemStack {
        val material = requireNotNull(Material.getMaterial(material)) { "Unknown material name \"$material\"" }
        return ItemStack(material).apply {
            itemMeta = createItemMeta(material)
        }
    }

    private fun createItemMeta(material: Material): ItemMeta? {
        val itemMeta = Bukkit.getItemFactory().getItemMeta(material) ?: return null

        (itemMeta as Damageable).damage = this.damage
        itemMeta.isUnbreakable = this.unbreakable

        this.displayName
            ?.takeIf(String::isNotEmpty)
            ?.let { itemMeta.displayName = it }

        // Lore colorizing
        if (this.lore.isNotEmpty()) {
            itemMeta.lore = this.lore.map { it.translateColorCodes() }
        }

        // Enchantments processing
        if (this.enchantments.isNotEmpty()) {
            this.enchantments.asSequence()
                .mapNotNull { (name, level) ->
                    val key = NamespacedKey.minecraft(name.toLowerCase())
                    Enchantment.getByKey(key)?.let { it to level }
                }
                .forEach { (enchantment, level) ->
                    itemMeta.addEnchant(enchantment, level, true)
                }
        }

        // ItemFlags processing
        if (this.itemFlags.isNotEmpty()) {
            val itemFlags = this.itemFlags.mapNotNull { safeValueOf<ItemFlag>(it) }
            itemMeta.addItemFlags(*itemFlags.toTypedArray())
        }

        return itemMeta
    }
}
