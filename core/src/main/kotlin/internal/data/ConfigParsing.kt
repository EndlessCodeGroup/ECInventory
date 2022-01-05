/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2022 EndlessCode Group and contributors
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

import ru.endlesscode.inventory.InventoryLayout.Companion.MAX_SLOT_POSITION

internal fun parseSlotPositions(value: String, errorPrefix: String = ""): IntRange {
    val parts = value.split("-", limit = 2).map { parseSlotPosition(it, errorPrefix) }
    val rangeStart = parts.first()
    val rangeEnd = parts.getOrElse(1) { rangeStart }
    require(rangeStart <= rangeEnd) {
        "$errorPrefix Slot positions range start should be lesser than range end, but it was '$value'. " +
                "Did you mean '$rangeEnd-$rangeStart'?".trimStart()
    }

    return rangeStart..rangeEnd
}

internal fun parseSlotPosition(value: String, errorPrefix: String = ""): Int {
    val position = requireNotNull(value.toIntOrNull()) {
        "$errorPrefix Slot position should be a number, but it was '$value'.".trimStart()
    }
    require(position in 0..MAX_SLOT_POSITION) {
        "$errorPrefix Slot position should be in range 0..$MAX_SLOT_POSITION, but it was '$position'.".trimStart()
    }
    return position
}
