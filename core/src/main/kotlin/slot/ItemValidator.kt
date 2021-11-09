package ru.endlesscode.inventory.slot

import org.bukkit.inventory.ItemStack

/**
 * Check is given item is valid.
 * Used to check if item can be placed to a slot.
 */
fun interface ItemValidator {
    fun isValid(item: ItemStack): Boolean

    companion object {
        val any: ItemValidator = ItemValidator { true }
    }
}
