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

import kotlinx.serialization.hocon.Hocon
import ru.endlesscode.inventory.internal.DI
import ru.endlesscode.inventory.misc.makeSureDirectoryExists
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists

/**
 * ConfigurationHolder helps to load and save [config].
 *
 * @param configsDirectory Path that points to a configs directory.
 * @param serializer           A loader used to load and save config.
 */
internal class ConfigurationHolder<T : Any>(
    configsDirectory: Path,
    private val serializer: ConfigurationSerializer<T>,
    private val hocon: Hocon = DI.hocon,
) {

    var config: T
        private set

    private val configPath: Path

    init {
        try {
            configsDirectory.makeSureDirectoryExists()

            configPath = configsDirectory.resolve("${serializer.fileName}.$CONFIG_EXTENSION")
            if (configPath.notExists()) configPath.createFile()

            config = loadConfig()
            save()
        } catch (e: Exception) {
            configError("Failed to initialize configuration!", e)
        }
    }

    /** Reloads [config]. */
    fun reload() {
        try {
            config = loadConfig()
        } catch (e: Exception) {
            configError("Failed to reload configuration!", e)
        }
    }

    private fun loadConfig(): T {
        return hocon.decodeFromFile(serializer, configPath)
    }

    /** Saves [config]. */
    fun save() {
        try {
            hocon.encodeToFile(serializer, config, configPath)
        } catch (e: Exception) {
            configError("Failed to save configuration!", e)
        }
    }
}
