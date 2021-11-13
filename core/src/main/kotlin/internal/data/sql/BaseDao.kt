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

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.DataSource

/** Provides DSL for DAOs. */
internal abstract class BaseDao(private val dataSource: DataSource) {

    /** Performs multiple statements as one transaction. */
    protected inline fun <T> transaction(crossinline block: Connection.() -> T): T {
        return dataSource.connection.use { connection ->
            connection.autoCommit = false
            val result: T
            try {
                result = connection.block()
                connection.commit()
            } catch (e: SQLException) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
            result
        }
    }

    protected inline fun <T> statement(sql: String, crossinline block: PreparedStatement.() -> T): T {
        return dataSource.statement(sql.trimIndent(), block)
    }
}
