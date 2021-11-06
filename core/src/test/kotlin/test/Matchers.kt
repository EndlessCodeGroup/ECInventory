package ru.endlesscode.rpginventory.test

import io.mockk.MockKMatcherScope
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.util.AIR

fun MockKMatcherScope.air() = item(AIR)
fun MockKMatcherScope.item(value: ItemStack): ItemStack = match { item ->
    value === item || value.type == item.type
}
