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

package ru.endlesscode.inventory.internal.data

import kotlinx.serialization.Serializable
import ru.endlesscode.inventory.InventoryLayout
import ru.endlesscode.inventory.InventoryLayout.Companion.MAX_ROWS
import ru.endlesscode.inventory.InventoryLayoutImpl
import ru.endlesscode.inventory.slot.Slot

@Serializable
internal data class InventoryConfig(
    val name: String,
    val defaultSlot: String,
    val slots: Map<String, String>,
    val rows: Int? = null,
) {

    fun parseInventoryLayout(id: String, knownSlots: Map<String, Slot>): InventoryLayout {
        val prefix = "Parsing inventory '$id':"
        val defaultSlot = knownSlots.getSlot(defaultSlot, prefix)
        require(slots.isNotEmpty()) { "$prefix Slots should not be empty." }

        val slotMap = sortedMapOf<Int, Slot>()
        slots.forEach { (key, slotName) ->
            val slotIds = parseSlotPositions(key, prefix)
            slotIds.forEach { slotId -> slotMap[slotId] = knownSlots.getSlot(slotName, prefix) }
        }

        val minRows = InventoryLayout.getMinimalRows(slotMap.lastKey() + 1)
        val rows = rows ?: minRows
        require(rows in 1..MAX_ROWS) { "$prefix Rows should be in range 1..$MAX_ROWS, but was $rows." }
        require(rows >= minRows) {
            "$prefix Minimal rows required for the given slots is $minRows, but $rows passed."
        }

        return InventoryLayoutImpl(
            id = id,
            name = name,
            defaultSlot = defaultSlot,
            slotsMap = slotMap,
            rows = rows,
        )
    }

    private fun Map<String, Slot>.getSlot(name: String, errorPrefix: String): Slot {
        return requireNotNull(get(name)) { "$errorPrefix Unknown slot name '$name'.".trimStart() }
    }
}
