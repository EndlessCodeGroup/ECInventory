/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2021 EndlessCode Group and contributors
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

import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

/**
 * Represents slot.
 *
 * @property id Identifier of the slot.
 * @property name A localized name of the slot.
 * @property texture The item that will be placed to the slot when it is empty.
 * @property type Slot type.
 * @property maxStackSize The maximum stack size for an ItemStack in this slot.
 */
public interface Slot {
    public val id: String
    public val name: String
    public val description: List<String>
    public val texture: ItemStack
    public val type: Type
    public val contentValidator: ItemValidator
    public val maxStackSize: Int

    @Serializable(with = SlotTypeSerializer::class)
    public enum class Type {
        /**
         * Indicates that the slot should be counted on stats counting.
         * @see RPGInventory.getPassiveSlots
         */
        PASSIVE,

        /**
         * Indicates that the slot used just to store items.
         * @see RPGInventory.getStorageSlots
         */
        STORAGE,

        /**
         * The slot isn't storage and shouldn't be counted on stats counting.
         * @see RPGInventory.getActiveSlots
         */
        ACTIVE
    }
}
