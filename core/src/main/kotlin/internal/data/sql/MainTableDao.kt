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

import java.sql.SQLException
import javax.sql.DataSource

// language=SQL
internal class MainTableDao(dataSource: DataSource) : BaseDao(dataSource) {

    /**
     * Returns database version stored in main table or `-1` if the version is not defined
     * or the main table doesn't exist.
     */
    fun getVersion(): Int {
        return statement(
            """
            SELECT $COLUMN_VERSION
            FROM $MAIN_TABLE
            WHERE $COLUMN_ID = $DEFAULT_ID
            LIMIT 1;
            """
        ) {
            try {
                val resultSet = executeQuery()
                if (resultSet.next()) resultSet.getInt(COLUMN_VERSION) else -1
            } catch (_: SQLException) {
                -1
            }
        }
    }

    private companion object {
        const val MAIN_TABLE = "${Database.TABLE_PREFIX}main"

        const val COLUMN_VERSION = "version"
        const val COLUMN_ID = "id"

        const val DEFAULT_ID = 42
    }
}
