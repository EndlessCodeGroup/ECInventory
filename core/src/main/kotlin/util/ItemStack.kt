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

package ru.endlesscode.rpginventory.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private val air by lazy { ItemStack(Material.AIR) }

/**
 * Global instance of AIR item stack.
 */
internal val AIR: ItemStack get() = air

/**
 * Returns itself or [AIR] if the item stack is null.
 */
internal fun ItemStack?.orEmpty(): ItemStack = this ?: AIR

/**
 * Returns `true` if the item stack is AIR.
 */
internal fun ItemStack.isEmpty(): Boolean = type == Material.AIR

/**
 * Returns `true` if the item stack isn't `null` and isn't AIR.
 */
internal fun ItemStack.isNotEmpty(): Boolean = type != Material.AIR

/**
 * Returns `true` if the item stack is `null` or AIR.
 */
internal fun ItemStack?.isNullOrEmpty(): Boolean = this == null || type == Material.AIR
