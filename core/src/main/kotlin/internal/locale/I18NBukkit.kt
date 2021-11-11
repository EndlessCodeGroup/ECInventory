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

package ru.endlesscode.inventory.internal.locale

import org.bukkit.ChatColor
import ru.endlesscode.inventory.internal.util.translateColorCodes
import java.nio.file.Path

internal class I18NBukkit(workDir: Path, initLocale: String) : I18N(workDir, initLocale) {

    override fun stripColor(message: String): String = checkNotNull(ChatColor.stripColor(message))

    override fun translateCodes(message: String): String = message.translateColorCodes()
}
