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

package ru.endlesscode.inventory.test

import org.bukkit.event.inventory.*
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.util.AIR

class TestInventoryClickEvent(
    view: InventoryView = TestInventoryView(),
    action: InventoryAction = InventoryAction.NOTHING,
    slot: Int = 0,
    slotType: InventoryType.SlotType = InventoryType.SlotType.CONTAINER,
    click: ClickType = ClickType.LEFT,
    hotbarKey: Int = -1,
) : InventoryClickEvent(view, slotType, slot, click, action, hotbarKey) {

    private var currentItem: ItemStack? = AIR

    override fun getCurrentItem(): ItemStack? = currentItem
    override fun setCurrentItem(stack: ItemStack?) {
        currentItem = stack
    }
}

class TestInventoryDragEvent(
    view: InventoryView = TestInventoryView(),
    oldCursor: ItemStack,
    newCursor: ItemStack? = null,
    rightClick: Boolean = false,
    slots: Map<Int, ItemStack> = mapOf(0 to oldCursor),
) : InventoryDragEvent(view, newCursor, oldCursor, rightClick, slots)
