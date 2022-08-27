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

package ru.endlesscode.inventory.test

import io.mockk.every
import io.mockk.mockk
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.util.AIR

fun Inventory(inv: CustomInventory) = Inventory(inv.size, inv)

fun Inventory(
    size: Int,
    holder: InventoryHolder? = null,
): Inventory {
    val contents = Array(size) { AIR }
    return mockk(relaxUnitFun = true) {
        every { getSize() } returns size
        every { getHolder() } answers { holder ?: SimpleInventoryHolder(this@mockk) }
        every { getItem(any()) } answers { contents[firstArg()] }
        every { setItem(any(), any()) } answers { contents[firstArg()] = secondArg() }
    }
}

// Required because of the issue: https://github.com/mockk/mockk/issues/868
private class SimpleInventoryHolder(private val inventory: Inventory) : InventoryHolder {
    override fun getInventory(): Inventory = inventory
}
