/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal

import kotlinx.serialization.hocon.Hocon
import org.bukkit.plugin.Plugin
import ru.endlesscode.inventory.InventoryLayout
import ru.endlesscode.inventory.internal.config.ConfigurationHolder
import ru.endlesscode.inventory.internal.data.DataHolder
import ru.endlesscode.inventory.internal.data.MainConfigurationImpl
import ru.endlesscode.inventory.internal.data.sql.Database
import ru.endlesscode.inventory.internal.data.sql.SqliteDatabaseConfig
import ru.endlesscode.inventory.internal.locale.I18N
import ru.endlesscode.inventory.internal.locale.I18NBukkit
import ru.endlesscode.inventory.slot.Slot
import ru.endlesscode.mimic.bukkit.load
import ru.endlesscode.mimic.items.BukkitItemsRegistry

internal object DI {

    private lateinit var plugin: Plugin
    private val servicesManager get() = plugin.server.servicesManager
    private val dataPath get() = plugin.dataFolder.toPath()

    val scheduler: TaskScheduler by lazy { PluginTaskScheduler(plugin) }

    private val itemsRegistry: BukkitItemsRegistry by lazy { checkNotNull(servicesManager.load()) }

    val hocon: Hocon by lazy { Hocon { useConfigNamingConvention = true } }
    val database: Database by lazy { Database(SqliteDatabaseConfig(dataPath)) }

    private val configHolder by lazy { ConfigurationHolder(dataPath, MainConfigurationImpl.SERIALIZER) }
    private val dataHolder by lazy { DataHolder(itemsRegistry, dataPath) }

    val locale: I18N by lazy { I18NBukkit(dataPath, config.locale) }
    val config: MainConfigurationImpl get() = configHolder.config
    val slots: Map<String, Slot> get() = dataHolder.slots
    val inventories: Map<String, InventoryLayout> get() = dataHolder.inventories

    fun init(plugin: Plugin) {
        this.plugin = plugin
    }

    fun reload() {
        configHolder.reload()
        dataHolder.reload()
        locale.reload(config.locale)
    }
}
