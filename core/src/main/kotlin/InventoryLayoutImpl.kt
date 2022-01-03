/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021-2022 EndlessCode Group and contributors
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

import ru.endlesscode.inventory.InventoryLayout.Companion.MAX_ROWS
import ru.endlesscode.inventory.slot.Slot
import java.util.*

internal data class InventoryLayoutImpl(
    override val id: String,
    override val name: String,
    override val defaultSlot: Slot,
    override val slotsMap: SortedMap<Int, Slot>,
    override val rows: Int = InventoryLayout.getMinimalRows(slotsMap.lastKey() + 1),
) : InventoryLayout {

    init {
        require(slotsMap.isNotEmpty()) { "Slots map shouldn't be empty." }
        require(rows <= MAX_ROWS) { "Inventory can't contain more that $MAX_ROWS rows, but passed $rows rows." }

        val minRows = InventoryLayout.getMinimalRows(slotsMap.lastKey() + 1)
        require(rows >= minRows) { "Minimal rows required for the given slots is $minRows, but $rows passed." }
    }
}
