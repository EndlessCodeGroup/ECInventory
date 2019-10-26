/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.configuration

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ru.endlesscode.rpginventory.misc.makeSureDirectoryExists
import ru.endlesscode.rpginventory.misc.mergeFiles
import java.io.File
import java.io.IOException
import java.nio.file.Path

/**
 * Collects configurations from given [configurationsDirectory].
 * @see collect
 */
class ConfigurationCollector(private val configurationsDirectory: Path) {

    constructor(dataDirectory: File) : this(dataDirectory.toPath())

    init {
        checkConfigurationDirectory()
    }

    @Suppress("UnstableApiUsage")
    fun <T> collect(typeToken: TypeToken<T>): T {
        checkConfigurationDirectory()

        val mergedConfig = configurationsDirectory.mergeFiles { path ->
            path.fileName.toString().toLowerCase().endsWith(CONFIG_EXTENSION)
        }

        try {
            val loader = HoconConfigurationLoader.builder().setPath(mergedConfig).build()
            val loaded = loader.load()
            return loaded.getValue(typeToken)
        } catch (e: ObjectMappingException) {
            configError(e)
        } catch (e: IOException) {
            configError(e)
        }
    }

    private fun checkConfigurationDirectory() {
        try {
            configurationsDirectory.makeSureDirectoryExists()
        } catch (e: IOException) {
            configError("'${configurationsDirectory.fileName}' must be a directory.", e)
        }
    }
}
