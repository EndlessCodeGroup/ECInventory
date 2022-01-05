/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2022 EndlessCode Group and contributors
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

/**
 * Represents slot.
 *
 * @property name Name of the slot, used to identify slot "type".
 * @property displayName A localized name of the slot.
 * @property description A localized description of the slot.
 * @property texture The item that will be placed to the slot when it is empty.
 * @see InventorySlot
 */
public interface Slot {
    public val name: String
    public val displayName: String
    public val description: List<String>
    public val texture: ItemStack
    public val onClickListeners: List<InventorySlot.OnClickListener>
}

/**
 * Represents slot that may contain items.
 *
 * @property contentType The type of slot content.
 * @property contentValidator Determines what items this slot can contain
 * @property maxStackSize The maximum stack size for an ItemStack in this slot.
 * @see ContainerInventorySlot
 */
public interface ContainerSlot : Slot {
    public val contentType: SlotContentType
    public val contentValidator: ItemValidator
    public val maxStackSize: Int
}
