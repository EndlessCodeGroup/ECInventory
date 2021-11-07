package ru.endlesscode.rpginventory.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.inventory.InventoryAction.*
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.slot.*

/** Converts [InventoryInteractEvent] to [SlotInteraction] and passes it to inventory. */
internal class InventoryClicksRouter : Listener {

    private val InventoryEvent.customInventory: CustomInventory?
        get() = inventory.holder as? CustomInventory

    @EventHandler
    fun onInventoryClosed(event: InventoryCloseEvent) {
        val inventory = event.customInventory ?: return
        inventory.onClose()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onDrag(event: InventoryDragEvent) {
        val inventory = event.customInventory ?: return

        val slots = event.rawSlots
        val position = slots.first()
        if (slots.size > 1) {
            // Disallow drag inside CustomInventory
            if (slots.any { it in inventory }) event.isCancelled = true
        } else if (position in inventory) {
            // User slightly moved mouse, consider it was a click
            val slot = inventory.getSlotAt(position)
            if (slot == null) {
                event.isCancelled = true
                return
            }

            val interaction = event.toPlaceInteraction(slot)
            inventory.handleInteraction(interaction)
        }
    }

    private operator fun CustomInventory.contains(position: Int) = position < viewSize

    private fun InventoryDragEvent.toPlaceInteraction(slot: InventorySlot): PlaceSlotContent {
        val amount = if (type == DragType.SINGLE) 1 else oldCursor.amount
        return PlaceSlotContent(this, slot, oldCursor, amount)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onClick(event: InventoryClickEvent) {
        val inventory = event.customInventory ?: return
        val interaction = event.toInteraction(inventory) ?: return
        inventory.handleInteraction(interaction)
    }

    /** Converts this event to [SlotInteraction] or returns `null` if the event shouldn't be handled. */
    private fun InventoryClickEvent.toInteraction(inventory: CustomInventory): SlotInteraction? {
        val isCustomInventoryInteraction = clickedInventory?.holder == inventory

        return if (isCustomInventoryInteraction) {
            val clickedSlot = inventory.getSlotAt(rawSlot)

            // Prevent interaction with non-functional inventory slots
            if (clickedSlot == null) {
                isCancelled = true
                null
            } else {
                inventorySlotInteraction(clickedSlot)
            }
        } else {
            vanillaSlotInteraction()
        }
    }

    private fun InventoryClickEvent.inventorySlotInteraction(slot: InventorySlot): SlotInteraction? = when (action) {
        PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
        DROP_ALL_SLOT, DROP_ONE_SLOT,
        MOVE_TO_OTHER_INVENTORY -> TakeSlotContent.fromClick(this, slot)
        PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> PlaceSlotContent.fromClick(this, slot)
        HOTBAR_MOVE_AND_READD,
        HOTBAR_SWAP -> HotbarSwapSlotContent.fromClick(this, slot)

        // These events don't affect inventory, ignore them
        CLONE_STACK,
        DROP_ALL_CURSOR, DROP_ONE_CURSOR,
        NOTHING -> null

        // Unsupported actions for inventory slots
        COLLECT_TO_CURSOR, UNKNOWN -> {
            isCancelled = true
            null
        }
    }

    /*
     * TODO:
     *  - Shift + click
     */
    private fun InventoryClickEvent.vanillaSlotInteraction(): SlotInteraction? = when (action) {
        MOVE_TO_OTHER_INVENTORY -> TODO()

        COLLECT_TO_CURSOR -> {
            // Cancel this event if any item in inventory can be collected to cursor
            isCancelled = view.topInventory.contents.any { it.isSimilar(cursor) }
            null
        }

        else -> null
    }
}
