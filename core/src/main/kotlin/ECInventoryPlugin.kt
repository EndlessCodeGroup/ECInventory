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

package ru.endlesscode.inventory

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.endlesscode.inventory.internal.DI
import ru.endlesscode.inventory.listener.InventoryClicksRouter
import ru.endlesscode.inventory.misc.registerCommand
import ru.endlesscode.inventory.util.Log

/** This class is entry point to the plugin. */
class ECInventoryPlugin : JavaPlugin() {

    init {
        Log.init(logger)
        DI.init(this)
    }

    override fun onEnable() {
        if (!loadParts()) {
            pluginLoader.disablePlugin(this)
            return
        }

        registerCommand()

        server.pluginManager.registerEvents(InventoryClicksRouter(), this)
    }

    private fun loadParts(): Boolean {
        if (!DI.config.enabled) {
            Log.i("Plugin is disabled in config.")
            return false
        }

        return makeSure {
            if (DI.inventories.isEmpty()) {
                Log.i("Inventory configs not found, add it to 'data' folder")
                return@makeSure false
            }
            Log.i("Loaded ${DI.inventories.size} inventories and ${DI.slots.size} slots")
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
        Log.e("Error on plugin enable.", exception)
    }

    @Deprecated(
        message = "Use ECInventoryPlugin#getConfiguration instead of ECInventoryPlugin#getConfig()",
        level = DeprecationLevel.ERROR
    )
    override fun getConfig(): FileConfiguration {
        throw UnsupportedOperationException("Use ECInventoryPlugin#getConfiguration instead of ECInventoryPlugin#getConfig()")
    }
}
