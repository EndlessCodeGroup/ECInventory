package ru.endlesscode.inventory.test

import io.mockk.MockKMatcherScope
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.util.AIR

fun MockKMatcherScope.air() = item(AIR)
fun MockKMatcherScope.item(value: ItemStack): ItemStack = match { item ->
    value === item || value.type == item.type
}
