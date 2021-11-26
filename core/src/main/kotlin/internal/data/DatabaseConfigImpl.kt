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

package ru.endlesscode.inventory.internal.data

import kotlinx.serialization.Serializable
import ru.endlesscode.inventory.DatabaseConfig
import ru.endlesscode.inventory.SqlDriverType
import ru.endlesscode.inventory.SqlDriverType.MYSQL
import ru.endlesscode.inventory.SqlDriverType.SQLITE
import ru.endlesscode.inventory.internal.data.serialization.ConfigEnumSerializer
import ru.endlesscode.inventory.internal.data.sql.DataSourceProvider
import ru.endlesscode.inventory.internal.data.sql.MysqlDataSourceProvider
import ru.endlesscode.inventory.internal.data.sql.SqliteDataSourceProvider
import javax.sql.DataSource

@Serializable
internal data class DatabaseConfigImpl(
    @Serializable(with = SqlDriverSerializer::class)
    override val type: SqlDriverType = SQLITE,
    override val host: String = "localhost",
    override val port: Int = 3306,
    override val name: String = "database-name",
    override val username: String = "user-name",
    override val password: String = "user-password",
) : DatabaseConfig

internal fun DatabaseConfig.createDataSource(): DataSource = toDataSourceProvider().createDataSource()

internal fun DatabaseConfig.toDataSourceProvider(): DataSourceProvider = when (type) {
    SQLITE -> SqliteDataSourceProvider()
    MYSQL -> MysqlDataSourceProvider(
        host = host,
        port = port,
        databaseName = name,
        username = username.takeIf(String::isNotBlank),
        password = password.takeIf(String::isNotBlank),
    )
}

internal object SqlDriverSerializer : ConfigEnumSerializer<SqlDriverType>(
    serialName = SqlDriverType::class.java.canonicalName,
    values = enumValues(),
)
