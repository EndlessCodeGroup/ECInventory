package ru.endlesscode.rpginventory.item

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class ConfigurableBukkitItemStack : ConfigurableItemStack {

    companion object {
        fun from(cis: ConfigurableItemStack): ConfigurableBukkitItemStack {
            return ConfigurableBukkitItemStack(cis)
        }
    }

    private constructor() : super()

    private constructor(cis: ConfigurableItemStack) : super(cis)

    fun toItemStack(): ItemStack {
        val material = requireNotNull(Material.getMaterial(this.getMaterial())) {
            "Unknown material name \"${this.getMaterial()}\""
        }
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        (itemMeta as Damageable).damage = this.getDamage()
        itemMeta.isUnbreakable = this.isUnbreakable()

        val displayName = this.getDisplayName()
        if (displayName != null && displayName.isNotEmpty()) {
            itemMeta.displayName = ChatColor.translateAlternateColorCodes('&', displayName)
        }

        //Lore colorizing
        if (this.getLore().isNotEmpty()) {
            itemMeta.lore = this.getLore().map { line -> ChatColor.translateAlternateColorCodes('&', line) }
        }

        //Enchantments processing
        if (this.getEnchantments().isNotEmpty()) {
            this.getEnchantments().asSequence()
                .mapNotNull { (name, level) -> Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase())) to level }
                .forEach { (enchantment, level) -> itemMeta.addEnchant(enchantment, level, true) }
        }

        //ItemFlags processing
        if (this.getItemFlags().isNotEmpty()) {
            val itemFlags = this.getItemFlags().mapNotNull { flag ->
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
