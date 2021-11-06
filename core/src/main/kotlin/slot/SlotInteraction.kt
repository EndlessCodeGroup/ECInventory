package ru.endlesscode.rpginventory.slot

import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

internal sealed interface SlotInteraction {
    val event: InventoryClickEvent
    val slot: InventorySlot

    /** Applies result of interaction to the [event]. */
    fun apply(result: SlotInteractionResult) {
        when (result) {
            is SlotInteractionResult.Deny -> {
                event.isCancelled = true
            }

            is SlotInteractionResult.Change -> {
                if (!result.syncCursor) {
                    event.currentItem = result.cursorItem
                }
            }

            is SlotInteractionResult.Accept -> {
                // Event just passed
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
    val amount: Int = if (event.action == InventoryAction.PLACE_ONE) 1 else item.amount
}
