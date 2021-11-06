package ru.endlesscode.rpginventory.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.slot.InventorySlot
import ru.endlesscode.rpginventory.slot.PlaceSlotContent
import ru.endlesscode.rpginventory.slot.SlotInteraction
import ru.endlesscode.rpginventory.slot.TakeSlotContent

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
        event.customInventory ?: return
        // TODO: Handle drag event later
        event.isCancelled = true
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
                createSlotInteraction(clickedSlot)
            }
        } else {
            null
        }
    }

    /*
     * TODO:
     *  - Shift + click
     *  - Swap with hotbar/shield
     */
    private fun InventoryClickEvent.createSlotInteraction(slot: InventorySlot): SlotInteraction? = when (action) {
        PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
        DROP_ALL_SLOT, DROP_ONE_SLOT,
        MOVE_TO_OTHER_INVENTORY,
        COLLECT_TO_CURSOR -> TakeSlotContent(this, slot)
        PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> PlaceSlotContent(this, slot)
        HOTBAR_MOVE_AND_READD -> TODO()
        HOTBAR_SWAP -> TODO()
        CLONE_STACK,
        DROP_ALL_CURSOR, DROP_ONE_CURSOR,
        NOTHING, UNKNOWN -> null
    }
}
