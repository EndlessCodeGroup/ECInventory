/*
 * This file is part of RPGInventory.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation either version 3 of the License or
 * (at your option) any later version.
 *
 * RPGInventory is distributed in the hope that it will be useful
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory.  If not see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.item

/**
 * Represents item.
 *
 * It differs from ItemStack because it is just set of data that can be used to create ItemStack.
 */
interface Item {
    val material: String
    val damage: Int
    val displayName: String?
    val unbreakable: Boolean
    val lore: List<String>
    val enchantments: Map<String, Int>
    val itemFlags: List<String>
}
