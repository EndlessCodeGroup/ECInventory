package ru.endlesscode.rpginventory.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private val air by lazy { ItemStack(Material.AIR) }
val AIR: ItemStack get() = air

fun ItemStack?.orAir(): ItemStack = this ?: AIR

fun ItemStack?.isEmpty(): Boolean = this == null || this.type == Material.AIR

fun ItemStack?.isNotEmpty(): Boolean = this != null && type != Material.AIR
