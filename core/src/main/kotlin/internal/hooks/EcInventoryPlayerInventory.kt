/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal.hooks

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority.High
import ru.endlesscode.inventory.internal.data.repository.InventoriesRepository
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.slot.SlotContentType
import ru.endlesscode.mimic.ExperimentalMimicApi
import ru.endlesscode.mimic.MimicApiLevel.VERSION_0_8
import ru.endlesscode.mimic.inventory.BukkitPlayerInventory

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalMimicApi::class)
internal class EcInventoryPlayerInventory(
    player: Player,
    private val inventoriesRepository: InventoriesRepository,
) : BukkitPlayerInventory(player) {

    constructor(player: Player) : this(player, DI.data.inventoriesRepository)

    override val equippedItems: List<ItemStack>
        get() = collectEquippedItems(collectItems(SlotContentType.EQUIPMENT))

    override val storedItems: List<ItemStack>
        get() = collectStoredItems(collectItems(SlotContentType.GENERIC))

    private fun collectItems(type: SlotContentType): List<ItemStack> {
        return inventoriesRepository.getInventories(player)
            .flatMap { it.getContainerSlots(type) }
            .map { it.content }
            .toList()
    }

    companion object {
        fun hook(plugin: Plugin) {
            DI.mimic.registerPlayerInventoryProvider(::EcInventoryPlayerInventory, VERSION_0_8, plugin, priority = High)
        }
    }
}
