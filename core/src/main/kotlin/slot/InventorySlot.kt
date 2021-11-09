/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2021 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.slot

import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.slot.SlotInteractionResult.*
import ru.endlesscode.inventory.util.AIR
import ru.endlesscode.inventory.util.isEmpty
import ru.endlesscode.inventory.util.isNotEmpty

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

    /**
     * Max stack size can be placed to slot.
     * @see maxStackSize
     */
    val slotMaxStackSize: Int = prototype.maxStackSize

    /**
     * Returns max stack size can be placed to the slot.
     * If slot is not empty may return max stack size of content item if it is lesser than slot's max stack size.
     * @see slotMaxStackSize
     */
    override val maxStackSize: Int
        get() = if (isEmpty()) slotMaxStackSize else minOf(slotMaxStackSize, content.maxStackSize)

    var content: ItemStack = AIR
        set(value) {
            field = value
            if (value.isNotEmpty() && slotMaxStackSize > 0 && value.amount > slotMaxStackSize) {
                value.amount = slotMaxStackSize
            }

            // We need to sync this change with the inventory's view if it is open
            holder.syncSlotWithView(this)
        }

    init {
        require(prototype !is InventorySlot) { "InventorySlot can't be used as prototype" }
        updateHolderMaxStackSize()
    }

    /** Returns `true` if slot's content is empty. */
    fun isEmpty(): Boolean = content.isEmpty()

    /** Returns `true` if slot contains maximal possible amount of items. */
    fun isFull(): Boolean = !isEmpty() && content.amount >= maxStackSize

    /** Returns [content] if it isn't empty or [texture] otherwise. */
    fun getContentOrTexture(): ItemStack = if (isEmpty()) texture else content

    /** Swap content with the given [item]. */
    internal fun swapItem(item: ItemStack): SlotInteractionResult = when {
        item.isEmpty() && this.isEmpty() || item.amount > maxStackSize -> Deny
        item.isEmpty() -> takeItem()
        else -> placeItem(item)
    }

    /** Takes item from this slot and returns result of this interaction. */
    internal fun takeItem(amount: Int = content.amount): SlotInteractionResult {
        if (this.isEmpty()) return Deny

        return if (amount < content.amount) {
            content.amount -= amount
            Accept
        } else {
            Change(currentItemReplacement = content).also {
                content = AIR
            }
        }
    }

    /** Places the given [item] to this slot and returns result of this interaction. */
    internal fun placeItem(item: ItemStack, amount: Int = item.amount): SlotInteractionResult {
        if (item.isEmpty()) return Deny

        // Slot is empty, so we don't need to return slot content
        return if (this.isEmpty()) {
            val stack = item.clone()

            // More than a single stack! Keep extra items in cursor.
            if (amount > maxStackSize) {
                val cursor = item.clone()
                stack.amount = maxStackSize
                cursor.amount = item.amount - maxStackSize

                content = stack
                Change(currentItemReplacement = AIR, cursorReplacement = cursor)
            } else {
                // All items fit to the slot
                stack.amount = amount
                content = stack
                Change(currentItemReplacement = AIR)
            }
        } else if (item.isSimilar(content)) {
            // Item is similar to content, so we can try to append it to content
            if (this.isFull()) {
                // Stack already full, deny this interaction
                Deny
            } else if (content.amount + amount <= maxStackSize) {
                // There are enough place for all items
                content.amount += amount
                Accept
            } else {
                // We can place some items
                val cursor = item.clone()
                cursor.amount -= maxStackSize - content.amount

                content.amount = maxStackSize
                Change(cursorReplacement = cursor)
            }
        } else if (item.amount <= maxStackSize) {
            // Item is not similar and the slot already contain another item, so we can swap content and cursor
            content = item.clone()
            Accept
        } else {
            Deny
        }
    }

    override fun toString(): String {
        return "InventorySlot(id=$id, position=$position, content=$content)"
    }

    private fun updateHolderMaxStackSize() {
        if (holder.maxStackSize < slotMaxStackSize) {
            holder.maxStackSize = slotMaxStackSize
        }
    }
}
