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

import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.mimic.items.BukkitItemsRegistry

/**
 * Validator that uses wildcards to check if the given item is valid.
 * - `*` (asterisk) matches zero or more characters
 * - `?` (question mark) matches any single character
 *
 * Item should match any of "allowed" wildcards and match to none
 * of "denied" wildcards to be considered as valid.
 */
public class WildcardItemValidator internal constructor(
    allowed: List<String>,
    denied: List<String>,
    private val itemsRegistry: BukkitItemsRegistry,
) : ItemValidator {

    /**
     * Creates validator using list of [allowed] and [denied] wildcards.
     * By default, all items are allowed and none are denied.
     */
    public constructor(
        allowed: List<String> = listOf("*"),
        denied: List<String> = emptyList(),
    ) : this(allowed, denied, DI.itemsRegistry)

    private val allowedPatterns = allowed.map(::parseWildcard)
    private val deniedPatterns = denied.map(::parseWildcard)

    override fun isValid(item: ItemStack): Boolean {
        val itemId = itemsRegistry.getItemId(item) ?: return false
        return allowedPatterns.any { itemId matches it }
                && deniedPatterns.none { itemId matches it }
    }

    public companion object {

        internal fun parseWildcard(wildcard: String): Regex {
            return Regex.escape(wildcard)
                .replace("*", "\\E.*\\Q")
                .replace("?", "\\E.\\Q")
                .replace("\\Q\\E", "")
                .toRegex()
        }
    }
}
