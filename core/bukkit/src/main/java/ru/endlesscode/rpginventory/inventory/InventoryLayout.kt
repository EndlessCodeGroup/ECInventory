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

package ru.endlesscode.rpginventory.inventory

import org.bukkit.inventory.ItemStack
import java.util.SortedMap

/**
 * Layout of an inventory.
 *
 * @property name Name of an inventory.
 * @property filler The item that will be used to fill unassigned slots.
 * @property slotsMap The map of the slots. Sorted by key.
 */
interface InventoryLayout {
    val name: String
    val filler: ItemStack
    val slotsMap: SortedMap<Int, Slot>
}
