package ru.endlesscode.rpginventory.slot

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.slot.SlotInteractionResult.*
import ru.endlesscode.rpginventory.util.isNullOrEmpty
import ru.endlesscode.rpginventory.util.orEmpty

internal sealed interface InventoryInteraction {
    val event: InventoryInteractEvent
}

internal sealed interface SlotInteraction : InventoryInteraction {
    val slot: InventorySlot

    /** Applies result of interaction to the [event]. */
    fun apply(result: SlotInteractionResult) {
        when (result) {
            is Deny -> {
                event.isCancelled = true
            }

            is Change -> {
                if (result.currentItemReplacement != null) {
                    (event as? InventoryClickEvent)?.currentItem = result.currentItemReplacement
                }
            }

            is Accept -> {
                // Event just passed
            }
        }
    }

    fun syncCursor(cursorItem: ItemStack) {
        event.view.cursor = cursorItem
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

internal data class HotbarSwapSlotContent(
    override val event: InventoryClickEvent,
    override val slot: InventorySlot,
    val hotbarItem: ItemStack,
) : SlotInteraction {

    companion object {
        fun fromClick(event: InventoryClickEvent, slot: InventorySlot): HotbarSwapSlotContent {
            val item = if (event.click == ClickType.SWAP_OFFHAND) {
                event.view.player.inventory.itemInOffHand
            } else {
                event.view.bottomInventory.getItem(event.hotbarButton).orEmpty()
            }
            return HotbarSwapSlotContent(event, slot, item)
        }
    }
}

internal data class AddItemToInventory(
    override val event: InventoryClickEvent,
    val item: ItemStack,
) : InventoryInteraction {

    fun setSlotItem(item: ItemStack) {
        event.currentItem = item
    }

    companion object {
        fun fromClick(event: InventoryClickEvent): AddItemToInventory? {
            val item = event.currentItem
            if (item.isNullOrEmpty()) return null
            return AddItemToInventory(event, item)
        }
    }
}
