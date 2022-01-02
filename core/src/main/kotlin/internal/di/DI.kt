/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021-2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal.di

import kotlinx.serialization.hocon.Hocon
import org.bukkit.plugin.Plugin
import ru.endlesscode.inventory.internal.PluginTaskScheduler
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.util.DisabledPlaceholders
import ru.endlesscode.inventory.util.Placeholders
import ru.endlesscode.mimic.bukkit.load
import ru.endlesscode.mimic.items.BukkitItemsRegistry

internal object DI {

    private lateinit var plugin: Plugin
    val scheduler: TaskScheduler by lazy { PluginTaskScheduler(plugin) }

    private val servicesManager get() = plugin.server.servicesManager

    // Hooks
    val itemsRegistry: BukkitItemsRegistry by lazy { checkNotNull(servicesManager.load()) }
    var placeholders: Placeholders = DisabledPlaceholders()

    val hocon: Hocon by lazy { Hocon { useConfigNamingConvention = true } }
    val data: DataModule by lazy { DataModule(plugin.dataFolder.toPath()) }

    fun init(plugin: Plugin) {
        DI.plugin = plugin
    }
}
