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

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.DragType

/** Click types applicable to slots. */
public enum class SlotClickType {

    /** The left mouse button. */
    LEFT,

    /** Holding shift while pressing the left mouse button. */
    SHIFT_LEFT,

    /** The right mouse button */
    RIGHT,

    /** Holding shift while pressing the right mouse button. */
    SHIFT_RIGHT;

    /** Returns `true` if this click type represents a left click. */
    public val isLeftClick: Boolean
        get() = this == LEFT || this == SHIFT_LEFT

    /** Returns `true` if this click type represents a right click. */
    public val isRightClick: Boolean
        get() = this == RIGHT || this == SHIFT_RIGHT

    /** Returns `true` if this click type represents a Shift click. */
    public val isShiftClick: Boolean
        get() = this == SHIFT_LEFT || this == SHIFT_RIGHT

    public companion object {

        /** Returns equivalent for the given [clickType] or `null` if click type is not suppoerted. */
        public fun of(clickType: ClickType): SlotClickType? = when (clickType) {
            ClickType.LEFT, ClickType.CREATIVE -> LEFT
            ClickType.SHIFT_LEFT -> SHIFT_LEFT
            ClickType.RIGHT -> RIGHT
            ClickType.SHIFT_RIGHT -> SHIFT_RIGHT
            // Other click types are not supported yet
            else -> null
        }

        /** Returns equivalent for the given [dragType]. */
        public fun of(dragType: DragType): SlotClickType = when (dragType) {
            DragType.EVEN -> LEFT
            DragType.SINGLE -> RIGHT
        }
    }
}
