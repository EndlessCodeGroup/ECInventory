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
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

internal inline fun <T> DataSource.statement(sql: String, block: PreparedStatement.() -> T): Result<T> {
    return connection.use { it.statement(sql, block) }
}

internal inline fun <T> Connection.statement(sql: String, block: PreparedStatement.() -> T): Result<T> {
    return runCatching { prepareStatement(sql).use(block) }
        .onFailure { exception -> if (exception !is SQLException) throw exception }
}

internal fun ResultSet.asSequence(): Sequence<ResultSet> = sequence {
    while (next()) yield(this@asSequence)
    close()
}
