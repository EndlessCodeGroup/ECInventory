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

package ru.endlesscode.rpginventory.misc

import org.bukkit.ChatColor
import ru.endlesscode.rpginventory.RPGInventoryPlugin
import ru.endlesscode.rpginventory.util.translateColorCodes

class I18NBukkit(instance: RPGInventoryPlugin) : I18N(instance.dataFolder, instance.configuration.locale) {

    override fun stripColor(message: String): String = checkNotNull(ChatColor.stripColor(message))

    override fun translateCodes(message: String): String = message.translateColorCodes()
}
