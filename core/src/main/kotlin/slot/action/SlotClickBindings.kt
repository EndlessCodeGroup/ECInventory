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

package ru.endlesscode.inventory.slot.action

import org.bukkit.entity.Player
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.slot.InventorySlot
import ru.endlesscode.inventory.slot.SlotClickType
import ru.endlesscode.inventory.util.Placeholders

/** Implementation of [InventorySlot.OnClickListener] defined by the given bindings. */
public class SlotClickBindings @JvmOverloads constructor(
    bindings: List<SlotActionBinding>,
    private val placeholders: Placeholders = DI.placeholders,
) : InventorySlot.OnClickListener {

    private val actions = buildMap<ActionPredicate, List<String>> {
        for ((predicates, commands) in bindings) {
            for (predicate in predicates) put(predicate, getOrElse(predicate, ::emptyList) + commands)
        }
    }

    override fun onClick(slot: InventorySlot, player: Player, clickType: SlotClickType) {
        val commands = ActionPredicate.getMatching(clickType)
            .asSequence()
            .map(actions::get)
            .filterNotNull()
            .firstOrNull() ?: return

        for (command in commands) {
            val preparedCommand = placeholders.apply(command, player)
            player.performCommand(preparedCommand)
        }
    }
}
