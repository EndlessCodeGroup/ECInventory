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

package ru.endlesscode.inventory

import ru.endlesscode.inventory.slot.Slot
import java.util.*

/**
 * Layout of an inventory.
 *
 * @property id Layout ID. Can be considered as an inventory type.
 * @property name Name of an inventory.
 * @property defaultSlot The slot that will be used for unassigned slots.
 * @property slotsMap The map of the slots. Sorted by key.
 * @property rows The inventory size in rows. Should be in range 1..6.
 */
public interface InventoryLayout {
    public val id: String
    public val name: String
    public val defaultSlot: Slot
    public val slotsMap: SortedMap<Int, Slot>
    public val rows: Int

    public companion object {

        /** Number of slots in one inventory row. */
        public const val SLOTS_IN_ROW: Int = 9

        /** Maximal possible number of rows in inventory. */
        public const val MAX_ROWS: Int = 6

        /** Maximal possible inventory size. */
        public const val MAX_SIZE: Int = SLOTS_IN_ROW * MAX_ROWS

        /** Maximal possible slot position in inventory. */
        public const val MAX_SLOT_POSITION: Int = MAX_SIZE - 1

        /** Calculates minimal inventory size in rows wor the given [minSize] in slots. */
        public fun getMinimalRows(minSize: Int): Int {
            require(minSize in 1..MAX_SIZE) {
                "Inventory size should be in range 1..$MAX_SIZE, but it was $minSize."
            }
            return (minSize + SLOTS_IN_ROW - 1) / SLOTS_IN_ROW
        }
    }
}
