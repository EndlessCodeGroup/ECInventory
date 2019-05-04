package ru.endlesscode.rpginventory.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
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
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        (itemMeta as Damageable).damage = this.damage
        itemMeta.isUnbreakable = this.unbreakable

        this.displayName
            ?.takeIf(String::isNotEmpty)
            ?.let { itemMeta.displayName = it }

        //Lore colorizing
        if (this.lore.isNotEmpty()) {
            itemMeta.lore = this.lore.map { it.translateColorCodes() }
        }

        //Enchantments processing
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

        //ItemFlags processing
        if (this.itemFlags.isNotEmpty()) {
            val itemFlags = this.itemFlags.mapNotNull { safeValueOf<ItemFlag>(it) }
            itemMeta.addItemFlags(*itemFlags.toTypedArray())
        }

        itemStack.itemMeta = itemMeta

        return itemStack
    }
}
