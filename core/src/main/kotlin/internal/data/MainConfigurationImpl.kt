/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2021 EndlessCode Group and contributors
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
import ru.endlesscode.inventory.MainConfiguration
import ru.endlesscode.inventory.internal.config.ConfigurationSerializer

@Serializable
internal class MainConfigurationImpl(
    override var enabled: Boolean = true,
    override var locale: String = "en",
    override val database: DatabaseConfigImpl = DatabaseConfigImpl(),
) : MainConfiguration {

    internal companion object {
        val SERIALIZER = ConfigurationSerializer<MainConfigurationImpl>(
            fileName = "main",
            description = "ECInventory configuration",
        ) { config ->
            mapOf(
                "enabled" to config.enabled,
                "locale" to config.locale,
                "database" to mapOf(
                    "type" to config.database.type.name.lowercase(),
                    "host" to config.database.host,
                    "port" to config.database.port,
                    "name" to config.database.name,
                    "username" to config.database.username,
                    "password" to config.database.password,
                )
            )
        }
    }
}
