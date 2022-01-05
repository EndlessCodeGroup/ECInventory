/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.serializer
import java.nio.file.Path
import kotlin.io.path.writeText

internal inline fun <reified T : Any> ConfigurationSerializer(
    fileName: String,
    description: String? = null,
) = ConfigurationSerializer(
    serializer = serializer<T>(),
    fileName = fileName,
    description = description,
)

internal data class ConfigurationSerializer<T : Any>(
    val serializer: KSerializer<T>,
    val fileName: String,
    val description: String? = null,
)

internal fun <T : Any> Hocon.decodeFromFile(configSerializer: ConfigurationSerializer<T>, path: Path): T {
    val config = ConfigFactory.parseFile(path.toFile())
    return decodeFromConfig(configSerializer.serializer, config.resolve())
}

internal fun <T : Any> Hocon.encodeToFile(configSerializer: ConfigurationSerializer<T>, value: T, path: Path) {
    val config = encodeToConfig(configSerializer.serializer, value)
    path.writeText(config.root().render(configRenderOptions))
}

private val configRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false)
