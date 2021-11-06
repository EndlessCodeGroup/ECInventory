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

    /** Returns [content] if it isn't empty or [texture] otherwise. */
    fun getContentOrTexture(): ItemStack = if (isEmpty()) texture else content

    /** Returns the slot content or [AIR] if slot content can't be taken. */
    fun takeItem(): ItemStack {
        if (this.isEmpty()) return AIR

        return content.also {
            content = AIR
        }
    }

    /**
     * Places the given [item] to this slot and returns [ItemStack] that should be taken from the slot,
     * or [AIR] if none item should be taken.
     */
    fun placeItem(item: ItemStack): ItemStack {
        if (item.isEmpty()) return AIR

        return content.also {
            content = item.clone()
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
