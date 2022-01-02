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

package ru.endlesscode.inventory.util

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.util.editItemMeta

/** Applies placeholders to text. */
public fun interface Placeholders {

    public fun apply(text: String, player: OfflinePlayer?): String

    public fun apply(text: List<String>, player: OfflinePlayer?): List<String> = text.map { apply(it, player) }

    public fun apply(item: ItemStack, player: OfflinePlayer?): ItemStack = item.editItemMeta {
        setDisplayName(apply(displayName, player))
        lore = apply(lore.orEmpty(), player)
    }
}
