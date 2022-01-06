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

package ru.endlesscode.inventory.internal.data.sql

import ru.endlesscode.inventory.internal.data.sql.entity.InventorySqlEntity
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

//language=SQL
internal class InventoryDao(dataSource: DataSource) : BaseDao(dataSource) {

    fun addInventory(holderId: UUID, inventory: InventorySqlEntity) = transaction {
        statement(
            """
            INSERT
            INTO $INVENTORIES
            VALUES (?, ?, ?)
            """
        ) {
            setString(1, inventory.id.toString())
            setString(2, inventory.layout)
            setString(3, inventory.content)
            executeUpdate()
        }

        statement(
            """
            INSERT
            INTO $BINDINGS
            VALUES (?, ?)
            """
        ) {
            setString(1, holderId.toString())
            setString(2, inventory.id.toString())
            executeUpdate()
        }
    }

    fun updateInventories(inventories: List<InventorySqlEntity>) {
        if (inventories.isEmpty()) return
        statement(
            """
            UPDATE $INVENTORIES
            SET $LAYOUT = ?,
                $CONTENT = ?
            WHERE $INVENTORIES_ID = ?
            """
        ) {
            for (inventory in inventories) {
                setString(1, inventory.layout)
                setString(2, inventory.content)
                setString(3, inventory.id.toString())
                addBatch()
            }
            executeBatch()
        }
    }

    fun getInventories(holderId: UUID): Result<List<InventorySqlEntity>> {
        return statement(
            """
            SELECT $INVENTORIES.*
            FROM $INVENTORIES
                JOIN $BINDINGS ON $INVENTORIES_ID = $BINDINGS_INVENTORY_ID
            WHERE $BINDINGS_HOLDER_ID = ?
            """
        ) {
            setString(1, holderId.toString())

            executeQuery()
                .asSequence()
                .map { result -> result.getInventory() }
                .toList()
        }
    }

    private fun ResultSet.getInventory(): InventorySqlEntity {
        return InventorySqlEntity(
            id = UUID.fromString(getString(ID)),
            layout = getString(LAYOUT),
            content = getString(CONTENT),
        )
    }

    private companion object {
        const val ID = "id"
        const val LAYOUT = "layout"
        const val CONTENT = "content"

        const val INVENTORIES = "${Database.TABLE_PREFIX}inventories"
        const val INVENTORIES_ID = "$INVENTORIES.id"

        const val BINDINGS = "${Database.TABLE_PREFIX}bindings"
        const val BINDINGS_HOLDER_ID = "$BINDINGS.holder_id"
        const val BINDINGS_INVENTORY_ID = "$BINDINGS.inventory_id"
    }
}
