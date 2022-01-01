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

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.mimic.items.BukkitItemsRegistry

@OptIn(ExperimentalKotest::class)
internal class WildcardItemValidatorTest : FeatureSpec({

    feature("check item is valid") {

        val itemsRegistry = mockk<BukkitItemsRegistry>()

        // SUT
        val validator = WildcardItemValidator(
            allowed = listOf(
                "*_sword",
                "custom:sword?",
                "allowed:*",
            ),
            denied = listOf(
                "*denied",
                "cursed_sword",
            ),
            itemsRegistry = itemsRegistry,
        )

        withData(
            "diamond_sword" to true,
            "custom:swordX" to true,
            "custom:sword" to false,
            "cursed_sword" to false,
            "allowed:but_denied" to false,
        ) { (itemId, isValid) ->
            every { itemsRegistry.getItemId(any()) } returns itemId
            validator.isValid(ItemStack(Material.STICK)) shouldBe isValid
        }
    }

    feature("wildcard parsing") {
        withData(
            "" to Regex(""),
            "?" to Regex("."),
            "*" to Regex(".*"),
            "minecraft:*" to Regex("\\Qminecraft:\\E.*"),
            "*_sword" to Regex(".*\\Q_sword\\E"),
            "item?" to Regex("\\Qitem\\E."),
            ".*:item[1-9]?" to Regex("\\Q.\\E.*\\Q:item[1-9]\\E."),
        ) { (wildcard, regex) -> WildcardItemValidator.parseWildcard(wildcard) shouldBe regex }
    }
})
