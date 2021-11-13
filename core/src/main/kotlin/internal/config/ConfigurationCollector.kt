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

package ru.endlesscode.inventory.internal.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.serializer
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.util.makeSureDirectoryExists
import ru.endlesscode.inventory.internal.util.useFileTree
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

/**
 * Collects configurations from given [configurationsDirectory].
 * @see collect
 */
internal class ConfigurationCollector(
    private val configurationsDirectory: Path,
    private val hocon: Hocon = DI.hocon,
) {

    init {
        checkConfigurationDirectory()
    }

    /** Returns value of type [T] from merged config. */
    inline fun <reified T : Any> collect(): T = collect(hocon.serializersModule.serializer())

    /** Returns value of type [T] from merged config. */
    fun <T : Any> collect(deserializer: DeserializationStrategy<T>): T {
        checkConfigurationDirectory()

        try {
            val mergedConfig = configurationsDirectory.useFileTree { paths ->
                paths.filter { it.isRegularFile() && it.extension == CONFIG_EXTENSION }
                    .map(::parseConfig)
                    .fold(ConfigFactory.empty()) { mergedConfig, fileConfig -> mergedConfig.withFallback(fileConfig) }
            }

            return hocon.decodeFromConfig(deserializer, mergedConfig.resolve())
        } catch (e: SerializationException) {
            configError(e)
        } catch (e: IOException) {
            configError(e)
        }
    }

    private fun parseConfig(path: Path): Config = ConfigFactory.parseFile(path.toFile())

    private fun checkConfigurationDirectory() {
        try {
            configurationsDirectory.makeSureDirectoryExists()
        } catch (e: IOException) {
            configError("'${configurationsDirectory.fileName}' must be a directory.", e)
        }
    }
}
