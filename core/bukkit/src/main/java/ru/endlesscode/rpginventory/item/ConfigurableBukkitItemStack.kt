package ru.endlesscode.rpginventory.item

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class ConfigurableBukkitItemStack private constructor(cis: ConfigurableItemStack) : ConfigurableItemStack(cis) {

    companion object {
        fun from(cis: ConfigurableItemStack): ConfigurableBukkitItemStack {
            return ConfigurableBukkitItemStack(cis)
        }
    }

    fun toItemStack(): ItemStack {
        val material = requireNotNull(Material.getMaterial(material)) { "Unknown material name \"$material\"" }
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        (itemMeta as Damageable).damage = this.damage
        itemMeta.isUnbreakable = this.unbreakable

        val displayName = this.displayName
        if (displayName != null && displayName.isNotEmpty()) {
            itemMeta.displayName = ChatColor.translateAlternateColorCodes('&', displayName)
        }

        //Lore colorizing
        if (this.lore.isNotEmpty()) {
            itemMeta.lore = this.lore.map { line -> ChatColor.translateAlternateColorCodes('&', line) }
        }

        //Enchantments processing
        if (this.enchantments.isNotEmpty()) {
            this.enchantments.asSequence()
                .mapNotNull { (name, level) -> Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase())) to level }
                .forEach { (enchantment, level) -> itemMeta.addEnchant(enchantment, level, true) }
        }

        //ItemFlags processing
        if (this.itemFlags.isNotEmpty()) {
            val itemFlags = this.itemFlags.mapNotNull { flag ->
                try {
                    ItemFlag.valueOf(flag)
                } catch (e: IllegalArgumentException) {
                    //TODO: Print exception to the logger
                    null
                }
            }
            itemMeta.addItemFlags(*itemFlags.toTypedArray())
        }

        itemStack.itemMeta = itemMeta

        return itemStack
    }
}
