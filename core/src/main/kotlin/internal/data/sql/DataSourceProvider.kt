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

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ru.endlesscode.inventory.internal.di.DI
import java.nio.file.Path
import javax.sql.DataSource

internal sealed interface DataSourceProvider {
    fun createDataSource(): DataSource
}

internal class SqliteDataSourceProvider(
    private val dataPath: Path = DI.data.dataPath,
) : DataSourceProvider {

    override fun createDataSource(): DataSource {
        val config = HikariConfig().apply {
            poolName = POOL_NAME
            jdbcUrl = "jdbc:sqlite:$dataPath/db.sqlite"
        }
        return HikariDataSource(config)
    }
}

internal class MysqlDataSourceProvider(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val database: String,
) : DataSourceProvider {

    override fun createDataSource(): DataSource {

        val config = HikariConfig().apply {
            poolName = POOL_NAME
            jdbcUrl = "jdbc:mysql://$host:$port/$database"
            username = this@MysqlDataSourceProvider.username
            password = this@MysqlDataSourceProvider.password
        }
        return HikariDataSource(config)
    }
}

private const val POOL_NAME = "ECInventory Pool"
