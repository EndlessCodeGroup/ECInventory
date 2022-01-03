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

import kotlinx.serialization.Serializable
import ru.endlesscode.inventory.internal.data.serialization.ConfigEnumSerializer
import ru.endlesscode.inventory.internal.util.MAX_STACK_SIZE

@Serializable(with = SlotTypeSerializer::class)
public enum class SlotType(public val defaultStackSize: Int) {
    /** Indicates that the slot used just to store items. */
    STORAGE(defaultStackSize = MAX_STACK_SIZE),

    /** Indicates that the slot can store equipment. */
    EQUIPMENT(defaultStackSize = 1),

    /** Indicates that slot can't store items. */
    GUI(defaultStackSize = 0),
}

internal object SlotTypeSerializer : ConfigEnumSerializer<SlotType>(
    serialName = SlotType::class.java.canonicalName,
    values = enumValues(),
)
