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

import ru.endlesscode.inventory.DatabaseConfig
import ru.endlesscode.inventory.internal.data.createDataSource
import ru.endlesscode.inventory.internal.util.Log
import ru.endlesscode.inventory.internal.util.useResourceStream
import javax.sql.DataSource

internal class Database(config: DatabaseConfig) {

    private var dataSource: DataSource = config.createDataSource()

    val inventoryDao: InventoryDao by lazy { InventoryDao(dataSource) }

    fun init() {
        useResourceStream("/database/$VERSION.sql") { it.bufferedReader().readText() }
            .split(";")
            .asSequence()
            .filter(String::isNotBlank)
            .forEach { query -> dataSource.statement(query) { execute() } }
        Log.i("Database initialized.")
    }

    fun updateConfig(config: DatabaseConfig) {
        dataSource = config.createDataSource()
        inventoryDao.updateDataSource(dataSource)
    }

    companion object {
        /** Current database version. */
        const val VERSION = 1

        /** Prefix used for all tables. */
        const val TABLE_PREFIX = "ecinv_"
    }
}
