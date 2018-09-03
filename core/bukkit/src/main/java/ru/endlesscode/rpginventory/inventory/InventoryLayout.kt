package ru.endlesscode.rpginventory.inventory

import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Layout of an inventory.
 *
 * @property name Name of an inventory.
 * @property filler The item that will be used to fill unassigned slots.
 * @property slotsMap The map of the slots. Sorted by key.
 */
interface InventoryLayout {
    val name: String
    val filler: ItemStack
    val slotsMap: SortedMap<Int, Slot>
}
