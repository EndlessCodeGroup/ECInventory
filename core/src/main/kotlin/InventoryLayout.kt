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
import ru.endlesscode.inventory.slot.Slot
import java.util.*

/**
 * Layout of an inventory.
 *
 * @property id Layout ID. Can be considered as an inventory type.
 * @property name Name of an inventory.
 * @property emptySlotTexture The item that will be used to fill unassigned slots.
 * @property slotsMap The map of the slots. Sorted by key.
 */
public interface InventoryLayout {
    public val id: String
    public val name: String
    public val emptySlotTexture: ItemStack
    public val slotsMap: SortedMap<Int, Slot>

    public companion object {
        /**
         * Maximal possible slot position in inventory.
         * Calculated as 54 (number of slots in large chest) - 1 (indices starts at 0)
         */
        public const val MAX_SLOT_POSITION: Int = 53
    }
}
