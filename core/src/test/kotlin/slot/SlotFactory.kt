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

@file:Suppress("TestFunctionName")

package ru.endlesscode.inventory.slot

import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory

fun InventorySlot(
    id: String = "test-slot",
    name: String = "$id Name",
    description: List<String> = listOf("$id Description"),
    texture: Material = Material.AIR,
    type: Slot.Type = Slot.Type.STORAGE,
    contentValidator: ItemValidator = ItemValidator.any,
    maxStackSize: Int = 1,
    position: Int = 0,
    holder: CustomInventory = mockk(relaxed = true),
): InventorySlot {
    return InventorySlot(
        prototype = Slot(id, name, description, texture, type, contentValidator, maxStackSize),
        holder = holder,
        position = position,
    )
}

fun Slot(
    id: String = "test-slot",
    name: String = "$id Name",
    description: List<String> = listOf("$id Description"),
    texture: Material = Material.AIR,
    type: Slot.Type = Slot.Type.STORAGE,
    contentValidator: ItemValidator = ItemValidator.any,
    maxStackSize: Int = 1,
): Slot = SlotImpl(
    id = id,
    name = name,
    description = description,
    texture = ItemStack(texture),
    type = type,
    contentValidator = contentValidator,
    maxStackSize = maxStackSize,
)
