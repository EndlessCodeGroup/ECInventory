/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
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

package ru.endlesscode.inventory

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.InstantTaskScheduler
import ru.endlesscode.inventory.internal.listener.AddItemToInventory
import ru.endlesscode.inventory.internal.util.AIR
import ru.endlesscode.inventory.internal.util.orEmpty
import ru.endlesscode.inventory.slot.Slot
import ru.endlesscode.inventory.test.TestInventoryClickEvent
import ru.endlesscode.inventory.test.TestInventoryView
import ru.endlesscode.inventory.test.mockItemFactory
import java.util.*

class InventoryInteractionsTest : FeatureSpec({

    // SUT
    lateinit var inventory: CustomInventory

    val inventoryView = TestInventoryView()
    lateinit var event: TestInventoryClickEvent

    beforeSpec {
        mockItemFactory()
    }

    val maxStackSize = 4
    var slotsCounter = 0
    fun slot(): Slot = Slot(
        id = "slot${++slotsCounter}",
        maxStackSize = maxStackSize,
    )

    feature("add item to inventory") {

        fun initSlots(vararg slots: Int) {
            val inventoryLayout = InventoryLayoutImpl(
                id = "test",
                name = "Test",
                emptySlotTexture = AIR,
                slotsMap = slots.associateWith { slot() }.toSortedMap(),
            )
            inventory = spyk(CustomInventory(UUID.randomUUID(), inventoryLayout, InstantTaskScheduler()))
        }

        fun addItemToInventory(item: ItemStack) {
            event = TestInventoryClickEvent(inventoryView, MOVE_TO_OTHER_INVENTORY, slot = inventory.viewSize + 1)
            event.currentItem = item
            val interaction = AddItemToInventory(event, item)
            inventory.handleInteraction(interaction)
        }

        fun assertSlots(current: ItemStack, vararg pairs: Pair<Int, ItemStack>) {
            val expectedContent = pairs.toMap()
            inventory.getSlots().forAll { slot ->
                slot.content shouldBe expectedContent[slot.position].orEmpty()
            }
            event.currentItem shouldBe current
        }

        scenario("add item to empty slot") {
            initSlots(1)
            val item = ItemStack(Material.STICK, 2)
            addItemToInventory(item)

            assertSlots(current = AIR, 1 to item)
        }

        scenario("add item to two slots") {
            initSlots(1, 42)
            val item = ItemStack(Material.STICK, 6)
            addItemToInventory(item)

            assertSlots(
                current = AIR,
                1 to ItemStack(Material.STICK, 4),
                42 to ItemStack(Material.STICK, 2),
            )
        }

        scenario("add item to similar slot") {
            initSlots(1)
            inventory.findEmptySlot()?.content = ItemStack(Material.STICK, 2)
            val item = ItemStack(Material.STICK, 3)
            addItemToInventory(item)

            assertSlots(
                current = ItemStack(Material.STICK, 1),
                1 to ItemStack(Material.STICK, 4),
            )
        }

        scenario("there no empty slots") {
            initSlots(1)
            inventory.findEmptySlot()?.content = ItemStack(Material.STICK)
            val item = ItemStack(Material.BLAZE_ROD)
            addItemToInventory(item)

            assertSlots(
                current = item,
                1 to ItemStack(Material.STICK),
            )
        }
    }
})
