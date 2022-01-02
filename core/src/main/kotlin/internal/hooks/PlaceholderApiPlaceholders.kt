/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.PluginManager
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.util.Log
import ru.endlesscode.inventory.util.Placeholders

/** Applies placeholders using [PlaceholderAPI]. */
internal class PlaceholderApiPlaceholders : Placeholders {

    override fun apply(text: String, player: OfflinePlayer?): String {
        return PlaceholderAPI.setPlaceholders(player, text)
    }

    override fun apply(text: List<String>, player: OfflinePlayer?): List<String> {
        return PlaceholderAPI.setPlaceholders(player, text)
    }

    companion object {
        fun hook(pluginManager: PluginManager) {
            if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
                DI.placeholders = PlaceholderApiPlaceholders()
                Log.i("[Hook] PlaceholderAPI successfully hooked!")
            }
        }
    }
}
