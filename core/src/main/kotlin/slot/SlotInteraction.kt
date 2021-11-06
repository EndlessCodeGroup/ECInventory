package ru.endlesscode.rpginventory.slot

import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.slot.SlotInteractionResult.*

internal sealed interface SlotInteraction {
    val event: InventoryClickEvent
    val slot: InventorySlot

    /** Applies result of interaction to the [event]. */
    fun apply(result: SlotInteractionResult) {
        when (result) {
            is Deny -> {
                event.isCancelled = true
            }

            is Change -> {
                if (!result.syncCursor) {
                    event.currentItem = result.cursorItem
                }
            }

            is Accept -> {
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
) : SlotInteraction {

    val amount: Int = when (event.action) {
        PICKUP_ONE, DROP_ONE_SLOT -> 1
        PICKUP_HALF -> (slot.content.amount + 1) / 2
        else -> slot.content.amount
    }
}

internal data class PlaceSlotContent(
    override val event: InventoryClickEvent,
    override val slot: InventorySlot,
) : SlotInteraction {
    val item: ItemStack = checkNotNull(event.cursor) { "Cursor item shouldn't be null" }
    val amount: Int = if (event.action == PLACE_ONE) 1 else item.amount
}
