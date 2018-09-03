@file:JvmName("ItemUtils")
package ru.endlesscode.rpginventory.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private val air by lazy { ItemStack(Material.AIR) }

/**
 * Global instance of AIR item stack.
 */
internal val AIR: ItemStack get() = air

/**
 * Returns itself or [AIR] if the item stack is null.
 */
fun ItemStack?.orAir(): ItemStack = this ?: AIR

/**
 * Returns `true` if the item stack is `null` or AIR.
 */
fun ItemStack?.isEmpty(): Boolean = this == null || this.type == Material.AIR

/**
 * Returns `true` if the item stack isn't `null` and isn't AIR.
 */
fun ItemStack?.isNotEmpty(): Boolean = this != null && type != Material.AIR
