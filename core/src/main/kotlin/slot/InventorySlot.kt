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
import ru.endlesscode.inventory.internal.util.*

/**
 * Represents slot in the inventory, that has own behavior.
 *
 * @param prototype The prototype that used to determine slot's behavior.
 *
 * @property holder The inventory that contain this slot.
 * @property position Position of the slot in vanilla inventory.
 * @property content The item stored in the slot. Or air if the slot is empty.
 */
public class InventorySlot(
    prototype: Slot,
    public val holder: CustomInventory,
    public val position: Int,
) : Slot by prototype {

    override val texture: ItemStack = prototype.texture
        get() = field.clone()

    /**
     * Max stack size can be placed to slot.
     * @see maxStackSize
     */
    public val slotMaxStackSize: Int = prototype.maxStackSize

    /**
     * Returns max stack size can be placed to the slot.
     * If slot is not empty may return max stack size of content item if it is lesser than slot's max stack size.
     * @see slotMaxStackSize
     */
    override val maxStackSize: Int
        get() = if (isEmpty()) slotMaxStackSize else minOf(slotMaxStackSize, content.maxStackSize)

    public var content: ItemStack = AIR
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
    public fun isEmpty(): Boolean = content.isEmpty()

    /** Returns `true` if slot contains maximal possible amount of items. */
    public fun isFull(): Boolean = !isEmpty() && content.amount >= maxStackSize

    /** Returns [content] if it isn't empty or [texture] otherwise. */
    public fun getContentOrTexture(): ItemStack = if (isEmpty()) texture else content

    /** Swaps slot content with the given [item] and returns item taken from the slot. */
    public fun swapItem(item: ItemStack): ItemStack {
        return when {
            item.isEmpty() && this.isEmpty() || item.amount > maxStackSize -> item
            item.isEmpty() -> takeItem()
            this.isEmpty() -> placeItem(item)

            else -> {
                val takenItem = content
                content = item.cloneWithAmount()
                takenItem
            }
        }
    }

    /** Takes the given [amount] of items from the slot and returns the taken [ItemStack]. */
    public fun takeItem(amount: Int = content.amount): ItemStack {
        if (this.isEmpty()) return AIR

        // Take part of items from the slot
        return if (amount < content.amount) {
            val takenItems = content.clone()
            takenItems.amount = amount
            content.amount -= amount
            takenItems
        } else {
            // Take all items from the slot
            val takenItems = content
            content = AIR
            takenItems
        }
    }

    /**
     * Places the given [item] to the slot and returns leftover [ItemStack] not fitting to the slot.
     *
     * Returns the given [item] itself if it can't be placed to the slot.
     * The [amount] should not be lesser than `1` or grater that the number of items in the given `ItemStack`.
     *
     * @see swapItem
     */
    public fun placeItem(item: ItemStack, amount: Int = item.amount): ItemStack {
        require(amount in 1..item.amount) { "Amount should be in range 1..${item.amount} but was $amount." }
        if (item.isEmpty()) return item

        // Slot is empty, so we don't need to return slot content
        return if (this.isEmpty()) {
            val stack = item.clone()

            // More than a single stack! Return leftover items
            if (amount > maxStackSize) {
                val leftover = item.clone()
                stack.amount = maxStackSize
                leftover.amount = item.amount - maxStackSize

                content = stack
                leftover
            } else {
                // All items fit to the slot
                stack.amount = amount
                content = stack
                item - amount
            }
        } else if (item.isSimilar(content)) {
            // Item is similar to content, so we can try to append it to content
            if (this.isFull()) {
                // Stack already full, return item unchanged
                item
            } else if (content.amount + amount <= maxStackSize) {
                // There are enough place for all items
                content.amount += amount
                item - amount
            } else {
                // We can place some items
                val leftover = item.clone()
                leftover.amount -= maxStackSize - content.amount

                content.amount = maxStackSize
                leftover
            }
        } else {
            item
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
