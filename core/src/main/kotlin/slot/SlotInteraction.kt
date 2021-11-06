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
                if (result.currentItemReplacement != null) {
                    event.currentItem = result.currentItemReplacement
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
    override val event: InventoryInteractEvent,
    override val slot: InventorySlot,
    val amount: Int,
) : SlotInteraction {

    companion object {
        fun fromClick(event: InventoryClickEvent, slot: InventorySlot): TakeSlotContent {
            val amount: Int = when (event.action) {
                PICKUP_ONE, DROP_ONE_SLOT -> 1
                PICKUP_HALF -> (slot.content.amount + 1) / 2
                else -> slot.content.amount
            }
            return TakeSlotContent(event, slot, amount)
        }
    }
}

internal data class PlaceSlotContent(
    override val event: InventoryInteractEvent,
    override val slot: InventorySlot,
    val item: ItemStack,
    val amount: Int,
) : SlotInteraction {

    companion object {
        fun fromClick(event: InventoryClickEvent, slot: InventorySlot): PlaceSlotContent {
            val item: ItemStack = checkNotNull(event.cursor) { "Cursor item shouldn't be null" }
            val amount: Int = when (event.action) {
                PLACE_ONE -> 1
                SWAP_WITH_CURSOR -> if (event.isRightClick) 1 else item.amount
                else -> item.amount
            }
            return PlaceSlotContent(event, slot, item, amount)
        }
    }
}
