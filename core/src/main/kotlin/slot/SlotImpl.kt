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

package ru.endlesscode.inventory.slot

import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.util.MAX_STACK_SIZE

internal data class SlotImpl(
    override val name: String,
    override val displayName: String,
    override val description: List<String>,
    override val texture: ItemStack,
    override val onClickListeners: List<InventorySlot.OnClickListener>,
) : Slot

internal data class ContainerSlotImpl(
    override val name: String,
    override val displayName: String,
    override val description: List<String>,
    override val texture: ItemStack,
    override val onClickListeners: List<InventorySlot.OnClickListener>,
    override val contentType: SlotContentType,
    override val contentValidator: ItemValidator,
    override val maxStackSize: Int,
) : ContainerSlot {

    init {
        require(maxStackSize in 1..MAX_STACK_SIZE) {
            "Can't create slot '$name'. Max stack size should be in range 1..$MAX_STACK_SIZE, bit it was $maxStackSize."
        }
    }
}
