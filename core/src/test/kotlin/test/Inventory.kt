@file:Suppress("TestFunctionName")

package ru.endlesscode.rpginventory.test

import io.mockk.every
import io.mockk.mockk
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import ru.endlesscode.rpginventory.CustomInventory

fun Inventory(inv: CustomInventory) = Inventory(inv.viewSize, inv)

fun Inventory(
    size: Int,
    holder: InventoryHolder? = null
): Inventory {
    return mockk(relaxUnitFun = true) {
        every { getSize() } returns size
        every { getHolder() } answers { holder ?: InventoryHolder { this@mockk } }
    }
}
