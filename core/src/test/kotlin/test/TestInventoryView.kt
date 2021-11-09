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

import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.util.AIR

class TestInventoryView(
    private val topInventory: Inventory = Inventory(size = 54),
    private val bottomInventory: Inventory = Inventory(size = 36),
    private val type: InventoryType = InventoryType.CHEST,
) : InventoryView() {

    var offhandItem: ItemStack = AIR

    private var _cursor: ItemStack = AIR

    private val player = mockk<Player>(relaxUnitFun = true) {
        every { itemOnCursor } answers { _cursor }
        every { setItemOnCursor(any()) } answers { _cursor = firstArg() }
        every { inventory } returns mockk(relaxUnitFun = true) {
            every { itemInOffHand } answers { offhandItem }
            every { setItemInOffHand(any()) } answers { offhandItem = firstArg() }
        }
    }

    override fun getTopInventory(): Inventory = topInventory
    override fun getBottomInventory(): Inventory = bottomInventory
    override fun getType(): InventoryType = type
    override fun getTitle(): String = "TestInventoryView"
    override fun getPlayer(): HumanEntity = player
}
