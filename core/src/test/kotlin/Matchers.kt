package ru.endlesscode.rpginventory

import io.mockk.MockKMatcherScope
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.util.AIR

fun MockKMatcherScope.air() = item(AIR)
fun MockKMatcherScope.item(value: ItemStack): ItemStack = match { item -> value.type == item.type }
