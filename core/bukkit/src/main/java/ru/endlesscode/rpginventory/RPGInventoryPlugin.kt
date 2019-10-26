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

package ru.endlesscode.rpginventory

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.endlesscode.rpginventory.configuration.ConfigurationProvider
import ru.endlesscode.rpginventory.configuration.MainConfiguration
import ru.endlesscode.rpginventory.misc.I18N
import ru.endlesscode.rpginventory.misc.I18NBukkit
import java.util.logging.Level

/** This class is entry point to the plugin. */
class RPGInventoryPlugin : JavaPlugin() {

    val configuration: MainConfiguration
        get() = configProvider.config

    private lateinit var configProvider: ConfigurationProvider<MainConfiguration>
    private lateinit var locale: I18N

    override fun onEnable() {
        if (!loadParts()) {
            return
        }
        //TODO: Logic
    }

    private fun loadParts(): Boolean {
        return makeSure {
            this.configProvider = ConfigurationProvider(this.dataFolder, MainConfiguration::class.java)
            this.locale = I18NBukkit(this)
        }
    }

    private fun makeSure(action: () -> Unit): Boolean {
        return try {
            action()
            true
        } catch (e: Exception) {
            criticalError(e)
            false
        }
    }

    private fun criticalError(exception: Exception) {
        logger.log(Level.SEVERE, "Error on plugin enable.", exception)
        server.pluginManager.disablePlugin(this)
    }

    @Deprecated(
        message = "Use RPGInventoryPlugin#getConfiguration instead of RPGInventoryPlugin#getConfig()",
        level = DeprecationLevel.ERROR
    )
    override fun getConfig(): FileConfiguration {
        throw UnsupportedOperationException("Use RPGInventoryPlugin#getConfiguration instead of RPGInventoryPlugin#getConfig()")
    }
}
