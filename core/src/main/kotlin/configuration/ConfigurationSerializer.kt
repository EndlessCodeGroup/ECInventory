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

package ru.endlesscode.inventory.configuration

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValueFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.serializer
import java.nio.file.Path
import kotlin.io.path.writeText

internal inline fun <reified T : Any> ConfigurationSerializer(
    fileName: String,
    description: String? = null,
    noinline convertToMap: (T) -> Map<String, Any?>,
) = ConfigurationSerializer(
    serializer = serializer(),
    fileName = fileName,
    description = description,
    convertToMap = convertToMap,
)

internal data class ConfigurationSerializer<T : Any>(
    val serializer: KSerializer<T>,
    val fileName: String,
    val description: String? = null,
    val convertToMap: (T) -> Map<String, Any?>,
)

internal fun <T : Any> Hocon.decodeFromFile(configSerializer: ConfigurationSerializer<T>, path: Path): T {
    val config = ConfigFactory.parseFile(path.toFile())
    return decodeFromConfig(configSerializer.serializer, config.resolve())
}

@Suppress("unused") // Hocon will be used soon https://github.com/Kotlin/kotlinx.serialization/pull/1740
internal fun <T : Any> Hocon.encodeToFile(configSerializer: ConfigurationSerializer<T>, value: T, path: Path) {
    val configValue = ConfigValueFactory.fromAnyRef(configSerializer.convertToMap(value), configSerializer.description)
    check(configValue is ConfigObject)
    path.writeText(configValue.render(configRenderOptions))
}

private val configRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false)
