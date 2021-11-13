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

package ru.endlesscode.inventory

import org.bukkit.plugin.java.JavaPlugin
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.listener.InventoryClicksRouter
import ru.endlesscode.inventory.internal.registerCommand
import ru.endlesscode.inventory.internal.util.Log

/** This class is entry point to the plugin. */
public class ECInventoryPlugin : JavaPlugin() {

    private val config: MainConfiguration
        get() = DI.data.config

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
        if (!config.enabled) {
            Log.i("Plugin is disabled in internal.config.")
            return false
        }

        return makeSure {
            val data = DI.data
            if (data.inventories.isEmpty()) {
                Log.i("Inventory configs not found, add it to 'data' folder")
                return@makeSure false
            }
            Log.i("Loaded ${data.inventories.size} inventories and ${data.slots.size} slots")
            data.database.init()
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
}
