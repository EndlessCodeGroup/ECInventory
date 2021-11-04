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
import ru.endlesscode.rpginventory.internal.DI
import java.util.logging.Level

/** This class is entry point to the plugin. */
class RPGInventoryPlugin : JavaPlugin() {

    init {
        DI.init(this)
    }

    override fun onEnable() {
        if (!loadParts()) {
            pluginLoader.disablePlugin(this)
            return
        }
        //TODO: Logic
    }

    private fun loadParts(): Boolean {
        if (!DI.config.enabled) {
            logger.info("Plugin is disabled in config.")
            return false
        }

        return makeSure {
            if (DI.data.isEmpty()) {
                logger.info("Data configs not found, please add it to 'data' folder")
                return@makeSure false
            }
            logger.info("Loaded ${DI.data.inventories.size} inventories and ${DI.data.slots.size} slots")
            true
        }
    }

    private fun makeSure(action: () -> Boolean): Boolean {
        return try {
            action()
        } catch (e: Exception) {
            criticalError(e)
            false
        }
    }

    private fun criticalError(exception: Exception) {
        logger.log(Level.SEVERE, "Error on plugin enable.", exception)
    }

    @Deprecated(
        message = "Use RPGInventoryPlugin#getConfiguration instead of RPGInventoryPlugin#getConfig()",
        level = DeprecationLevel.ERROR
    )
    override fun getConfig(): FileConfiguration {
        throw UnsupportedOperationException("Use RPGInventoryPlugin#getConfiguration instead of RPGInventoryPlugin#getConfig()")
    }
}
