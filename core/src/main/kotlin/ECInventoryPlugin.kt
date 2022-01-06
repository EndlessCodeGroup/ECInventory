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

package ru.endlesscode.inventory

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.endlesscode.inventory.internal.compat.BukkitVersion
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.di.DataModule
import ru.endlesscode.inventory.internal.hook.PlaceholderApiPlaceholders
import ru.endlesscode.inventory.internal.listener.InventoryClicksRouter
import ru.endlesscode.inventory.internal.listener.PlayerInventoriesLoader
import ru.endlesscode.inventory.internal.registerCommand
import ru.endlesscode.inventory.internal.util.Log
import ru.endlesscode.inventory.internal.util.sendColorizedMessage

/** This class is entry point to the plugin. */
public class ECInventoryPlugin : JavaPlugin() {

    private val data: DataModule
        get() = DI.data
    private val config: MainConfiguration
        get() = data.config

    init {
        Log.init(logger)
        DI.init(this)
    }

    override fun onEnable() {
        if (!loadParts()) {
            pluginLoader.disablePlugin(this)
            return
        }

        registerCommand(this)

        server.pluginManager.registerEvents(InventoryClicksRouter(), this)
        server.pluginManager.registerEvents(PlayerInventoriesLoader(), this)
    }

    internal fun reload(sender: CommandSender) {
        sender.sendColorizedMessage("&7Reloading inventories configs...")
        data.reload()
        for (player in server.onlinePlayers) {
            data.inventoriesRepository.loadInventories(player)
        }
        sender.sendColorizedMessage("&2Configs reloaded successfully!")

        // Disable plugin if it was disabled in config.
        if (!checkPluginIsEnabled()) pluginLoader.disablePlugin(this)
    }

    private fun loadParts(): Boolean {
        if (!checkPluginIsEnabled()) return false
        if (!BukkitVersion.checkCompatibility()) return false

        return makeSure {
            initHooks()

            val data = data
            if (data.inventories.isEmpty()) {
                Log.i("Inventory configs not found, add it to 'data' folder")
                return@makeSure false
            }
            Log.i("Loaded ${data.inventories.size} inventories and ${data.slots.size} slots")
            data.database.init()
            true
        }
    }

    private fun checkPluginIsEnabled(): Boolean {
        return if (config.enabled) {
            true
        } else {
            Log.i("Plugin is disabled in config.")
            false
        }
    }

    private fun initHooks() {
        PlaceholderApiPlaceholders.hook(server.pluginManager)
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
