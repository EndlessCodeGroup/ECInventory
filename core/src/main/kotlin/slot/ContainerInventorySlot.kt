/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2022 EndlessCode Group and contributors
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

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.util.*
import ru.endlesscode.inventory.util.Placeholders

/**
 * Slot assigned to inventory, that can contain items.
 *
 * @param prototype The prototype that used to determine slot's behavior.
 */
public class ContainerInventorySlot(
    prototype: ContainerSlot,
    override val holder: CustomInventory,
    override val position: Int,
) : InventorySlot(prototype), ContainerSlot {

    /**
     * Max stack size allowed in this slot.
     * @see maxStackSize
     */
    public val slotMaxStackSize: Int = prototype.maxStackSize

    /**
     * Returns max stack size for an ItemStack in this slot.
     * If slot is not empty may return max stack size of content item if it is lesser than slot's max stack size.
     * @see slotMaxStackSize
     */
    override val maxStackSize: Int
        get() = if (isEmpty()) slotMaxStackSize else minOf(slotMaxStackSize, content.maxStackSize)

    override val contentType: SlotContentType by prototype::contentType
    override val contentValidator: ItemValidator by prototype::contentValidator

    /** The item stored in the slot. Returns `AIR` if the slot is empty. */
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
        updateHolderMaxStackSize()
        prototype.onClickListeners.forEach(::addOnClickListener)
    }

    /** Returns `true` if slot's content is empty. */
    public fun isEmpty(): Boolean = content.isEmpty()

    /** Returns `true` if slot contains maximal possible amount of items. */
    public fun isFull(): Boolean = !isEmpty() && content.amount >= maxStackSize

    /** Returns [content] if it isn't empty or [texture] otherwise. */
    override fun getView(placeholders: Placeholders, player: Player): ItemStack {
        val item = if (isEmpty()) texture else content
        return placeholders.apply(item, player)
    }

    /** Swaps slot content with the given [item] and returns item taken from the slot. */
    public fun swapItem(item: ItemStack): ItemStack = when {
        item.isEmpty() && this.isEmpty() -> item
        item.isEmpty() -> takeItem()
        item.amount > maxStackSize || !canHold(item) -> item
        this.isEmpty() -> placeItem(item)

        else -> {
            val takenItem = content
            content = item.cloneWithAmount()
            takenItem
        }
    }

    /** Takes the given [amount] of items from the slot and returns the taken [ItemStack]. */
    public fun takeItem(amount: Int = content.amount): ItemStack {
        if (this.isEmpty()) return AIR

        // Take part of items from the slot
        return if (amount < content.amount) {
            val takenItems = content.clone()
            takenItems.amount = amount
            changeContentAmount { it - amount }
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
        if (item.isEmpty() || !canHold(item)) return item

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
                changeContentAmount { it + amount }
                item - amount
            } else {
                // We can place some items
                val leftover = item.clone()
                leftover.amount -= maxStackSize - content.amount

                changeContentAmount { maxStackSize }
                leftover
            }
        } else {
            item
        }
    }

    /** Changes amount and synchronizes new content with inventory. */
    private fun changeContentAmount(change: (Int) -> Int) {
        content.amount = change(content.amount)
        content = content
    }

    /**
     * Returns `true` if this slot can hold the given [item].
     * Always returns `true` for `AIR` item.
     */
    public fun canHold(item: ItemStack): Boolean = item.isEmpty() || contentValidator.isValid(item)

    override fun toString(): String {
        return "ContainerInventorySlot(name=$name, position=$position, content=$content)"
    }

    private fun updateHolderMaxStackSize() {
        if (holder.maxStackSize < slotMaxStackSize) {
            holder.maxStackSize = slotMaxStackSize
        }
    }
}
