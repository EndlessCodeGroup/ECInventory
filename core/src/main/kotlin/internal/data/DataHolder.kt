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

package ru.endlesscode.inventory.internal.data

import kotlinx.serialization.hocon.Hocon
import ru.endlesscode.inventory.InventoryLayout
import ru.endlesscode.inventory.internal.config.ConfigurationCollector
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.util.Log
import ru.endlesscode.inventory.slot.Slot
import ru.endlesscode.mimic.items.BukkitItemsRegistry
import java.nio.file.Path

internal class DataHolder(
    private val itemsRegistry: BukkitItemsRegistry,
    private val collector: ConfigurationCollector,
) {

    constructor(
        pluginDataDir: Path,
        itemsRegistry: BukkitItemsRegistry = DI.itemsRegistry,
        hocon: Hocon = DI.hocon,
    ) : this(itemsRegistry, ConfigurationCollector(pluginDataDir.resolve("data"), hocon))

    var slots: Map<String, Slot> = emptyMap()
        private set
    var inventories: Map<String, InventoryLayout> = emptyMap()
        private set

    init {
        collectData()
    }

    fun reload() {
        collectData()
    }

    private fun collectData() {
        val data = collector.collect<DataConfig>()
        slots = data.slots.mapValues { (id, config) -> config.parseSlot(id, itemsRegistry) }
        inventories = data.inventories.mapValues { (id, config) -> config.parseInventoryLayout(id, slots) }

        val usedSlots = inventories.values
            .flatMap { it.slotsMap.values.asSequence().map(Slot::name) }
            .toSet()
        val unusedSlots = slots.keys - usedSlots
        if (unusedSlots.isNotEmpty()) Log.w("These slots are not used and could be removed: $unusedSlots.")
    }
}
