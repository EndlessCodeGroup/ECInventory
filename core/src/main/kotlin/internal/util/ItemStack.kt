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

package ru.endlesscode.inventory.internal.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private val air by lazy { ItemStack(Material.AIR, 0) }

/**
 * Global instance of AIR item stack.
 */
internal val AIR: ItemStack get() = air

/** Maximal possible item stack size. */
internal const val MAX_STACK_SIZE = 64

/**
 * Returns itself or [AIR] if the item stack is null.
 */
internal fun ItemStack?.orEmpty(): ItemStack = this ?: AIR

/**
 * Returns `true` if the item stack is AIR.
 */
internal fun ItemStack.isEmpty(): Boolean = type.isAir

/**
 * Returns `true` if the item stack isn't `null` and isn't AIR.
 */
internal fun ItemStack.isNotEmpty(): Boolean = !type.isAir

/**
 * Returns `true` if the item stack is `null` or AIR.
 */
@OptIn(ExperimentalContracts::class)
internal fun ItemStack?.isNullOrEmpty(): Boolean {
    contract {
        returns(false) implies (this@isNullOrEmpty != null)
    }

    return this == null || type.isAir
}

/** Clones [ItemStack] and sets the given [amount]. */
internal fun ItemStack.cloneWithAmount(amount: Int = this.amount): ItemStack {
    return clone().also { it.amount = amount }
}

/** Returns copy of the given [ItemStack] with amount decreased by the given [amount]. */
internal operator fun ItemStack.minus(amount: Int): ItemStack {
    return if (amount >= this.amount) AIR else cloneWithAmount(this.amount - amount)
}

/** Edit ItemStack meta. */
internal fun ItemStack.editItemMeta(block: ItemMeta.() -> Unit): ItemStack = apply {
    itemMeta = itemMeta?.apply(block)
}
