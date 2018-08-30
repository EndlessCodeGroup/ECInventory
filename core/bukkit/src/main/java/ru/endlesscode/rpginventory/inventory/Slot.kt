package ru.endlesscode.rpginventory.inventory

import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

/**
 * Represents slot.
 *
 * @property id Identifier of the slot.
 * @property localizedName Localized name of the slot.
 * @property slotHolder The holder that will be placed to the slot when it is empty.
 * @property type Type of the slot.
 * @property maxStackSize The the maximum stack size for an ItemStack in this slot.
 */
interface Slot {
    val id: String
    val localizedName: String
    val slotHolder: ItemStack
    val type: InventoryType.SlotType

    var maxStackSize: Int

    enum class Type {
        /**
         * Indicates that the slot should be counted on stats counting.
         * @see RPGInventory.getPassiveSlots
         */
        PASSIVE,
        /**
         * Indicates that the slot used just to store items.
         * @see RPGInventory.getStorageSlots
         */
        STORAGE,
        /**
         * The slot isn't storage and shouldn't be counted on stats counting.
         * @see RPGInventory.getActiveSlots
         */
        ACTIVE
    }
}
