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

package ru.endlesscode.inventory.slot

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.util.Placeholders

/** Inventory GUI slot, that can't contain items. */
public class GuiInventorySlot(
    prototype: Slot,
    override val holder: CustomInventory,
    override val position: Int,
) : InventorySlot(prototype) {

    /** Always returns [texture] as a view. */
    override fun getView(placeholders: Placeholders, player: Player): ItemStack {
        return placeholders.apply(texture, player)
    }

    override fun toString(): String {
        return "GuiInventorySlot(name=$name, position=$position)"
    }
}
