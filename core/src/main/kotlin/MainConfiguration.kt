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

package ru.endlesscode.inventory

/** ECInventory configuration.  */
public interface MainConfiguration {
    public val enabled: Boolean
    public val locale: String
    public val database: DatabaseConfig
}

public interface DatabaseConfig {
    public val type: SqlDriverType
    public val host: String
    public val port: Int
    public val name: String
    public val username: String
    public val password: String
}

/** Supported SQL drivers. */
public enum class SqlDriverType {
    SQLITE,
    MYSQL,
}
