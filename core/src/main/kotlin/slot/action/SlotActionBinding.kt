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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.endlesscode.inventory.internal.data.serialization.ConfigEnumSerializer
import ru.endlesscode.inventory.slot.SlotClickType

/** Binds the given [actions] to the given [predicates]. */
@Serializable
public data class SlotActionBinding(
    @SerialName("on") val predicates: Set<ActionPredicate>,
    @SerialName("do") val actions: List<String>,
) {

    init {
        check(predicates.isNotEmpty()) { "'on' should not be empty." }
        check(actions.isNotEmpty()) { "'do' should not be empty." }
    }
}

@Serializable(with = ActionPredicateSerializer::class)
public enum class ActionPredicate {
    CLICK,
    LEFT_CLICK,
    SHIFT_LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_RIGHT_CLICK;

    public companion object {
        /**
         * Returns predicates matching to the given [clickType] ordered by priority.
         * First element in returned list is most prior.
         */
        public fun getMatching(clickType: SlotClickType): List<ActionPredicate> = listOfNotNull(
            SHIFT_LEFT_CLICK.takeIf { clickType == SlotClickType.SHIFT_LEFT },
            LEFT_CLICK.takeIf { clickType.isLeftClick },
            SHIFT_RIGHT_CLICK.takeIf { clickType == SlotClickType.SHIFT_RIGHT },
            RIGHT_CLICK.takeIf { clickType.isRightClick },
            CLICK,
        )
    }
}

internal object ActionPredicateSerializer : ConfigEnumSerializer<ActionPredicate>(
    serialName = ActionPredicate::class.java.canonicalName,
    values = enumValues(),
)
