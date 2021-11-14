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

import org.bukkit.entity.Player
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.InventoryLayout
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.internal.data.DataHolder
import ru.endlesscode.inventory.internal.data.sql.InventoryDao
import ru.endlesscode.inventory.internal.data.sql.entity.toDomain
import ru.endlesscode.inventory.internal.data.sql.entity.toSqlEntity
import ru.endlesscode.inventory.internal.di.DI
import java.util.*

internal class InventoriesRepository(
    private val dao: InventoryDao = DI.data.database.inventoryDao,
    private val dataHolder: DataHolder = DI.data.dataHolder,
    private val scheduler: TaskScheduler = DI.scheduler,
) {

    private val layouts: Map<String, InventoryLayout>
        get() = dataHolder.inventories

    private val cache = InventoriesCache()

    fun getInventory(player: Player, type: String): CustomInventory = getInventory(player.uniqueId, type)

    fun getInventory(holderId: UUID, type: String): CustomInventory {
        val inventory = cache.getInventories(holderId).find { it.type == type }
        return inventory ?: createInventory(holderId, type)
    }

    private fun createInventory(holderId: UUID, type: String): CustomInventory {
        val inventory = CustomInventory(getLayout(type))
        cache.addInventory(holderId, inventory)
        scheduler.runAsync { dao.addInventory(holderId, inventory.toSqlEntity()) }
        return inventory
    }

    fun loadInventories(player: Player) = loadInventories(player.uniqueId)

    fun loadInventories(holderId: UUID) {
        scheduler.runAsync {
            val holderInventories = dao.getInventories(holderId).asSequence()
                .map { it.toDomain(getLayout(it.layout)) }
                .associateBy { it.id }

            runOnMain { cache.setInventories(holderId, holderInventories) }
        }
    }

    private fun getLayout(id: String): InventoryLayout = requireNotNull(layouts[id]) {
        "Unexpected inventory type '$id'. Available types: ${layouts.keys}"
    }

    fun unloadInventories(player: Player) = unloadInventories(player.uniqueId)

    fun unloadInventories(holderId: UUID) {
        val inventories = cache.removeInventories(holderId)
            .map { it.toSqlEntity() }
            .toList()
        scheduler.runAsync { dao.updateInventories(inventories) }
    }
}
