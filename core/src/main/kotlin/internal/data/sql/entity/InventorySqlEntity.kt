/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal.data.sql.entity

import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.InventoryLayout
import java.util.*

internal class InventorySqlEntity(
    val id: UUID,
    val layout: String,
    val content: String = "",
)

// FIXME: Add content serialization
internal fun CustomInventory.toSqlEntity(): InventorySqlEntity = InventorySqlEntity(id, type)

// FIXME: Add content deserialization
internal fun InventorySqlEntity.toDomain(layout: InventoryLayout): CustomInventory = CustomInventory(id, layout)
