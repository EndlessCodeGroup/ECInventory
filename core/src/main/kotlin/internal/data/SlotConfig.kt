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

import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.data.SlotConfigType.*
import ru.endlesscode.inventory.internal.util.Log
import ru.endlesscode.inventory.internal.util.MAX_STACK_SIZE
import ru.endlesscode.inventory.internal.util.isEmpty
import ru.endlesscode.inventory.internal.util.orEmpty
import ru.endlesscode.inventory.slot.*
import ru.endlesscode.inventory.slot.action.SlotActionBinding
import ru.endlesscode.mimic.items.BukkitItemsRegistry

@Serializable
internal data class SlotConfig(
    val name: String = "",
    val description: List<String> = emptyList(),
    val texture: String? = null,
    val type: SlotConfigType = GENERIC,
    val actions: List<SlotActionBinding> = emptyList(),
    val allowedItems: List<String> = listOf("*"),
    val deniedItems: List<String> = emptyList(),
    val maxStackSize: Int = type.defaultStackSize,
) {

    fun parseSlot(id: String, itemsRegistry: BukkitItemsRegistry): Slot {
        val prefix = "Parsing slot '$id':"
        val textureItem = texture?.let {
            requireNotNull(itemsRegistry.getItem(it)) {
                "$prefix Unknown texture '$texture'. $errorMimicIdExplanation"
            }
        }.orEmpty()

        if (textureItem.isEmpty() && (name.isNotEmpty() || description.isNotEmpty())) {
            Log.w(
                "$prefix 'name' and 'description' is present but 'texture' is not specified.",
                "Slot name and description can't be shown without texture.",
            )
        }

        return when (type) {
            GENERIC, EQUIPMENT -> createContainerSlot(id, textureItem, prefix)
            GUI -> createGuiSlot(id, textureItem, prefix)
        }
    }

    private fun createContainerSlot(id: String, texture: ItemStack, prefix: String): Slot {
        val correctMaxStackSize = maxStackSize.coerceIn(1, MAX_STACK_SIZE)
        if (correctMaxStackSize != maxStackSize) {
            Log.w(
                "$prefix max stack size should be in range 1..$MAX_STACK_SIZE, but was '$maxStackSize'.",
                "Will be used $correctMaxStackSize instead, please fix slot config.",
            )
        }

        val contentType = when (type) {
            GENERIC -> SlotContentType.GENERIC
            EQUIPMENT -> SlotContentType.EQUIPMENT
            else -> error("$prefix Unexpected slot type '$type'.")
        }

        return ContainerSlotImpl(
            id = id,
            name = name,
            description = description,
            texture = texture,
            contentType = contentType,
            contentValidator = WildcardItemValidator(allowedItems, deniedItems),
            maxStackSize = correctMaxStackSize,
        )
    }

    private fun createGuiSlot(id: String, texture: ItemStack, prefix: String): Slot {
        val redundantOptions = mapOf(
            "allowed-items" to { allowedItems.singleOrNull() == "*" },
            "denied-items" to { deniedItems.isEmpty() },
            "max-stack-size" to { maxStackSize == GUI.defaultStackSize },
        ).filterValues { isDefault -> !isDefault() }.keys

        if (redundantOptions.isNotEmpty()) {
            Log.w(
                "$prefix These options are not applicable to slots with type GUI and may be removed:",
                "  ${redundantOptions.joinToString()}",
            )
        }

        return SlotImpl(
            id = id,
            name = name,
            description = description,
            texture = texture,
        )
    }
}
