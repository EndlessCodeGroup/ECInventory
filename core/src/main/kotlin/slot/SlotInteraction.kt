package ru.endlesscode.rpginventory.slot

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

internal sealed interface SlotInteraction {
    val event: InventoryClickEvent
    val slot: InventorySlot

    /** Applies result of interaction to the [event]. */
    fun apply(result: SlotInteractionResult) {
        when (result) {
            is SlotInteractionResult.Cancel -> {
                event.isCancelled = true
            }

            is SlotInteractionResult.Success -> {
                if (!result.syncCursor) {
                    event.currentItem = result.cursorItem
                }
            }
        }
    }

    fun syncCursor(cursorItem: ItemStack) {
        @Suppress("DEPRECATION") // It is ok because syncCursor is called
        event.cursor = cursorItem
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
