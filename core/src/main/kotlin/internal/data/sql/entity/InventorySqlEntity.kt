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

package ru.endlesscode.inventory.internal.data.sql.entity

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.InventoryLayout
import ru.endlesscode.inventory.slot.ContainerInventorySlot
import java.util.*

internal class InventorySqlEntity(
    val id: UUID,
    val layout: String,
    val content: String = "",
)

internal fun CustomInventory.toSqlEntity(): InventorySqlEntity {
    val yaml = YamlConfiguration()
    getSlotsMap()
        .forEach { (slotName, slots) ->
            slots.asSequence()
                .filterIsInstance<ContainerInventorySlot>()
                .filterNot(ContainerInventorySlot::isEmpty)
                .forEachIndexed { n, slot -> yaml["$slotName:$n"] = slot.content }
        }

    return InventorySqlEntity(id, type, yaml.saveToString())
}

internal fun InventorySqlEntity.toDomain(layout: InventoryLayout, holder: Player): CustomInventory {
    val yaml = YamlConfiguration.loadConfiguration(content.reader())
    val inventory = CustomInventory(id, layout, holder)
    yaml.getKeys(false).forEach { key ->
        val parts = key.split(":", limit = 2)
        val slotName = parts.first()
        val n = parts.getOrNull(1)?.toIntOrNull() ?: 0
        inventory.setItem(slotName, n, yaml.getItemStack(key))
    }
    return inventory
}
