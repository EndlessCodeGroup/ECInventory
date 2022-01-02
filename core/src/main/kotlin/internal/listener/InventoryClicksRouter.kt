/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
 *
 * ECInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ECInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ECInventory. If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.inventory.internal.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.inventory.InventoryAction.*
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.slot.InventorySlot

/** Converts [InventoryInteractEvent] to [SlotInteraction] and passes it to inventory. */
internal class InventoryClicksRouter(
    private val scheduler: TaskScheduler = DI.scheduler,
) : Listener {

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
            val interaction = event.toPlaceInteraction(slot)
            inventory.handleInteraction(interaction)
        }
    }

    private operator fun CustomInventory.contains(position: Int) = position < size

    private fun InventoryDragEvent.toPlaceInteraction(slot: InventorySlot): PlaceSlotContent {
        val amount = if (type == DragType.SINGLE) 1 else oldCursor.amount
        return PlaceSlotContent(this, slot, oldCursor, amount)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onClick(event: InventoryClickEvent) {
        val inventory = event.customInventory ?: return
        val interaction = event.toInteraction(inventory) ?: return
        inventory.handleInteraction(interaction)

        // We should manually sync offhand slot after SWAP_OFFHAND event
        // Issue: https://hub.spigotmc.org/jira/browse/SPIGOT-6145
        if (event.click == ClickType.SWAP_OFFHAND) {
            val playerInventory = event.whoClicked.inventory
            scheduler.runOnMain { playerInventory.setItemInOffHand(playerInventory.itemInOffHand) }
        }
    }

    /** Converts this event to [SlotInteraction] or returns `null` if the event shouldn't be handled. */
    private fun InventoryClickEvent.toInteraction(inventory: CustomInventory): InventoryInteraction? {
        val isCustomInventoryInteraction = clickedInventory?.holder == inventory

        return if (isCustomInventoryInteraction) {
            val clickedSlot = inventory.getSlotAt(rawSlot)
            inventorySlotInteraction(clickedSlot)
        } else {
            vanillaSlotInteraction()
        }
    }

    private fun InventoryClickEvent.inventorySlotInteraction(slot: InventorySlot): SlotInteraction? = when (action) {
        PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
        DROP_ALL_SLOT, DROP_ONE_SLOT,
        MOVE_TO_OTHER_INVENTORY -> TakeSlotContent.fromClick(this, slot)
        PLACE_ALL, PLACE_SOME, PLACE_ONE -> PlaceSlotContent.fromClick(this, slot)
        HOTBAR_MOVE_AND_READD,
        HOTBAR_SWAP -> SwapSlotContent.fromClick(this, slot)

        SWAP_WITH_CURSOR -> if (slot.isEmpty()) {
            PlaceSlotContent.fromClick(this, slot)
        } else {
            SwapSlotContent.fromClick(this, slot)
        }

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

    private fun InventoryClickEvent.vanillaSlotInteraction(): InventoryInteraction? = when (action) {
        MOVE_TO_OTHER_INVENTORY -> {
            // Will handle this event ourselves
            isCancelled = true
            AddItemToInventory.fromClick(this)
        }

        COLLECT_TO_CURSOR -> {
            // Cancel this event if any item in inventory can be collected to cursor
            isCancelled = view.topInventory.contents
                .asSequence()
                .filterNotNull()
                .any { it.isSimilar(cursor) }
            null
        }

        else -> null
    }
}
