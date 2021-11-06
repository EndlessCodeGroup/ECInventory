/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.util.AIR
import ru.endlesscode.rpginventory.util.isEmpty
import ru.endlesscode.rpginventory.util.isNotEmpty

/**
 * Represents slot in the inventory, that has own behavior.
 *
 * @param prototype The prototype that used to determine slot's behavior.
 *
 * @property holder The inventory that contain this slot.
 * @property position Position of the slot in vanilla inventory.
 * @property content The item stored in the slot. Or air if the slot is empty.
 */
class InventorySlot(
    prototype: Slot,
    val holder: CustomInventory,
    val position: Int,
) : Slot by prototype {

    override val texture: ItemStack = prototype.texture
        get() = field.clone()

    var content: ItemStack = AIR
        set(value) {
            field = value
            if (value.isNotEmpty() && maxStackSize > 0 && value.amount > maxStackSize) {
                value.amount = maxStackSize
            }

            // We need to sync this change with the inventory's view if it is open
            //holder.syncSlotWithView(this)
        }

    init {
        require(prototype !is InventorySlot) { "InventorySlot can't be used as prototype" }
        updateHolderMaxStackSize()
    }

    /** Returns `true` if slot's content is empty. */
    fun isEmpty(): Boolean = content.isEmpty()

    /** Returns `true` if slot contains maximal possible amount of items. */
    fun isFull(): Boolean = !isEmpty() && content.amount == maxStackSize

    /** Returns [content] if it isn't empty or [texture] otherwise. */
    fun getContentOrTexture(): ItemStack = if (isEmpty()) texture else content

    /** Takes item from this slot and returns result of this interaction. */
    fun takeItem(amount: Int): SlotInteractionResult {
        if (this.isEmpty()) return SlotInteractionResult.Deny

        return if (amount < content.amount) {
            content.amount -= amount
            SlotInteractionResult.Accept
        } else {
            SlotInteractionResult.Change(content, syncSlot = texture.isNotEmpty()).also {
                content = AIR
            }
        }
    }

    /** Places the given [item] to this slot and returns result of this interaction. */
    fun placeItem(item: ItemStack, amount: Int): SlotInteractionResult {
        if (item.isEmpty()) return SlotInteractionResult.Deny

        // Slot is empty, so we don't need to return slot content
        return if (this.isEmpty()) {
            val stack = item.clone()

            // More than a single stack! Keep extra items in cursor.
            if (amount > maxStackSize) {
                val cursor = item.clone()
                stack.amount = maxStackSize
                cursor.amount = item.amount - maxStackSize

                content = stack
                SlotInteractionResult.Change(cursor, syncCursor = true, syncSlot = true)
            } else {
                // All items fit to the slot
                stack.amount = amount
                content = stack
                SlotInteractionResult.Change(AIR)
            }
        } else if (item.isSimilar(content)) {
            // Item is similar to content, so we can try to append it to content
            if (this.isFull()) {
                // Stack already full, deny this interaction
                SlotInteractionResult.Deny
            } else if (content.amount + amount <= maxStackSize) {
                // There are enough place for all items
                content.amount += amount
                SlotInteractionResult.Accept
            } else {
                // We can place some items
                val cursor = item.clone()
                cursor.amount -= maxStackSize - content.amount

                content.amount = maxStackSize
                SlotInteractionResult.Change(cursor, syncCursor = true, syncSlot = true)
            }
        } else {
            // Item is not similar and slot already contain another item, so we can swap content and cursor
            if (item.amount <= maxStackSize) {
                content = item.clone()
                SlotInteractionResult.Accept
            } else {
                SlotInteractionResult.Deny
            }
        }
    }

    override fun toString(): String {
        return "InventorySlot(id=$id, position=$position, content=$content)"
    }

    private fun updateHolderMaxStackSize() {
        if (holder.maxStackSize < maxStackSize) {
            holder.maxStackSize = maxStackSize
        }
    }
}
