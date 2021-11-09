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

package ru.endlesscode.inventory

import org.bukkit.inventory.ItemStack

/**
 * Class copied from CraftBukkit
 */
class InventoryIterator internal constructor(
    private val inventory: CustomInventory,
    private var nextIndex: Int = 0,
) : MutableListIterator<ItemStack> {

    private var lastDirection = Direction.NOT_MOVED

    override fun hasNext(): Boolean = nextIndex < inventory.size

    override fun next(): ItemStack {
        lastDirection = Direction.FORWARD
        return inventory.getItem(nextIndex++)
    }

    override fun nextIndex(): Int = nextIndex

    override fun hasPrevious(): Boolean = nextIndex > 0

    override fun previous(): ItemStack {
        lastDirection = Direction.BACKWARD
        return inventory.getItem(--nextIndex)
    }

    override fun previousIndex(): Int = nextIndex - 1

    override fun set(element: ItemStack) {
        if (lastDirection == Direction.NOT_MOVED) error("No current item!")

        val i = if (lastDirection == Direction.FORWARD) nextIndex - 1 else nextIndex
        inventory.setItem(i, element)
    }

    override fun add(element: ItemStack) {
        sizeChangeUnsupported()
    }

    override fun remove() {
        sizeChangeUnsupported()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun sizeChangeUnsupported(): Nothing {
        throw UnsupportedOperationException("Can't change the size of an inventory!")
    }

    private enum class Direction {
        FORWARD,
        BACKWARD,
        NOT_MOVED
    }
}
