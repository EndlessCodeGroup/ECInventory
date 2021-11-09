@file:Suppress("TestFunctionName")

package ru.endlesscode.inventory.test

import io.mockk.every
import io.mockk.mockk
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.util.AIR

fun Inventory(inv: CustomInventory) = Inventory(inv.viewSize, inv)

fun Inventory(
    size: Int,
    holder: InventoryHolder? = null,
): Inventory {
    val contents = Array(size) { AIR }
    return mockk(relaxUnitFun = true) {
        every { getSize() } returns size
        every { getHolder() } answers { holder ?: InventoryHolder { this@mockk } }
        every { getItem(any()) } answers { contents[firstArg()] }
        every { setItem(any(), any()) } answers { contents[firstArg()] = secondArg() }
    }
}
