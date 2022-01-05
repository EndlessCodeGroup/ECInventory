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
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.util.editItemMeta
import ru.endlesscode.inventory.internal.util.setDisplayNameAllowingEmpty
import ru.endlesscode.inventory.internal.util.translateColorCodes
import ru.endlesscode.inventory.util.Placeholders

/** Represents inventory slot. */
public abstract class InventorySlot : Slot {

    /** The inventory that contain this slot. */
    public abstract val holder: CustomInventory

    /** Position of the slot in the inventory. */
    public abstract val position: Int

    private val _onClickListeners = mutableListOf<OnClickListener>()

    override val onClickListeners: List<OnClickListener>
        get() = _onClickListeners

    /** Returns stack that should be used as a slot view for the given [player]. */
    public abstract fun getView(placeholders: Placeholders, player: Player): ItemStack

    /** Returns texture items with configured name and lore. */
    protected fun prepareTexture(texture: ItemStack): ItemStack = texture.clone().editItemMeta {
        setDisplayNameAllowingEmpty(displayName.translateColorCodes())
        lore = description.translateColorCodes()
        addItemFlags(*ItemFlag.values())
    }

    /**
     * Called when [player] clicked the slot.
     * @see addOnClickListener
     * @see removeOnClickListener
     */
    public open fun onClick(player: Player, clickType: SlotClickType) {
        onClickListeners.forEach { it.onClick(this, player, clickType) }
    }

    /**
     * Adds a [listener] to be invoked when this slot is clicked.
     * @see onClick
     * @see removeOnClickListener
     */
    public fun addOnClickListener(listener: OnClickListener) {
        _onClickListeners.add(listener)
    }

    /**
     * Removes the given [listener] from this slot if present.
     * @return `true` if the listener has been successfully removed; false otherwise
     * @see onClick
     * @see addOnClickListener
     */
    public fun removeOnClickListener(listener: OnClickListener): Boolean {
        return _onClickListeners.remove(listener)
    }

    /** Callback to be invoked when this slot is clicked. */
    public fun interface OnClickListener {

        /** Called when the [slot] is clicked by the [player]. */
        public fun onClick(slot: InventorySlot, player: Player, clickType: SlotClickType)
    }
}
