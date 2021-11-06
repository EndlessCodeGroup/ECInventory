package ru.endlesscode.rpginventory.slot

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

internal sealed interface SlotInteraction {
    val event: InventoryClickEvent
    val slot: InventorySlot

    fun cancel() {
        event.isCancelled = true
    }
}

internal data class TakeSlotContent(
    override val event: InventoryClickEvent,
    override val slot: InventorySlot,
) : SlotInteraction

internal data class PlaceSlotContent(
    override val event: InventoryClickEvent,
    override val slot: InventorySlot,
) : SlotInteraction {

    val item: ItemStack = checkNotNull(event.cursor) { "Cursor item shouldn't be null" }
}
