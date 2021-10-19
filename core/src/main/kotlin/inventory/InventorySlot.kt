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

package ru.endlesscode.rpginventory.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.extensions.isNullOrEmpty

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
    val position: Int
) : Slot by prototype {

    var content: ItemStack = ItemStack(Material.AIR)
        set(value) {
            field = content
            if (!value.isNullOrEmpty() && this.maxStackSize > 0 && value.amount > this.maxStackSize) {
                value.amount = this.maxStackSize
            }

            // We need to sync this change with the inventory's view if it is open
            holder.syncSlotWithView(this)
        }

    override var maxStackSize: Int = 1
        set(value) {
            field = value
            updateHolderMaxStackSize()
        }

    init {
        require(prototype !is InventorySlot) { "InventorySlot can't be used as prototype" }
        updateHolderMaxStackSize()
    }

    /** Returns true if slot's content is empty. */
    fun isEmpty(): Boolean = content.isNullOrEmpty()

    /** Returns [content] if it isn't empty and [slotTexture] otherwise. */
    fun getContentOrTexture(): ItemStack = if (isEmpty()) slotTexture else content

    private fun updateHolderMaxStackSize() {
        if (holder.maxStackSize < maxStackSize) {
            holder.maxStackSize = maxStackSize
        }
    }
}
