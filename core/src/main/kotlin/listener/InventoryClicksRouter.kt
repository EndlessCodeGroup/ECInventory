package ru.endlesscode.rpginventory.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent
import ru.endlesscode.rpginventory.CustomInventory
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

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val inventory = event.customInventory ?: return
        val interaction = event.toInteraction(inventory) ?: return
        inventory.handleInteraction(interaction)
    }

    /** Converts this event to [SlotInteraction] or returns `null` if the event shouldn't be handled. */
    private fun InventoryClickEvent.toInteraction(inventory: CustomInventory): SlotInteraction? {
        val isCustomInventoryInteraction = rawSlot == slot
        val clickedSlot = inventory.getSlotAt(rawSlot)

        // Prevent interaction with non-functional inventory slots
        if (clickedSlot == null) {
            isCancelled = true
            return null
        }

        return when (action) {
            PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
            DROP_ALL_CURSOR, DROP_ONE_CURSOR, DROP_ALL_SLOT, DROP_ONE_SLOT, -> {
                // If player tries to take items from vanilla inventory just ignore it
                if (isCustomInventoryInteraction) TakeSlotContent(this, clickedSlot) else null
            }

            PLACE_ALL -> TODO()
            PLACE_SOME -> TODO()
            PLACE_ONE -> TODO()
            SWAP_WITH_CURSOR -> TODO()
            MOVE_TO_OTHER_INVENTORY -> TODO()
            HOTBAR_MOVE_AND_READD -> TODO()
            HOTBAR_SWAP -> TODO()
            CLONE_STACK -> TODO()
            COLLECT_TO_CURSOR -> TODO()
            NOTHING, UNKNOWN -> null
        }
    }
}
