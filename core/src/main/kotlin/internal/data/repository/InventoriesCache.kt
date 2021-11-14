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

package ru.endlesscode.inventory.internal.data.repository

import ru.endlesscode.inventory.CustomInventory
import java.util.*

internal class InventoriesCache {

    private val inventories = mutableMapOf<UUID, CustomInventory>()
    private val bindings = mutableMapOf<UUID, Set<UUID>>()

    fun getInventories(holderId: UUID): Sequence<CustomInventory> {
        return bindings[holderId].orEmpty()
            .asSequence()
            .map(inventories::getValue)
    }

    fun addInventory(holderId: UUID, inventory: CustomInventory) {
        inventories[inventory.id] = inventory
        bindings[holderId] = bindings.getOrElse(holderId, ::emptySet) + inventory.id
    }

    fun setInventories(holderId: UUID, holderInventories: Map<UUID, CustomInventory>) {
        inventories.putAll(holderInventories)
        bindings[holderId] = holderInventories.keys
    }

    fun removeInventories(holderId: UUID): Sequence<CustomInventory> {
        return bindings.remove(holderId)
            .orEmpty()
            .asSequence()
            .mapNotNull(inventories::remove)
    }
}
