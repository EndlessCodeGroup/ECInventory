package ru.endlesscode.rpginventory

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemFactory

fun mockItemFactory() {
    val itemFactory = mockk<ItemFactory> {
        every { equals(null, null) } returns true
    }

    mockkStatic(Bukkit::class)
    every { Bukkit.getItemFactory() } returns itemFactory
}
