/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021-2022 EndlessCode Group and contributors
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

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.spyk
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.InstantTaskScheduler
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.listener.PlaceSlotContent
import ru.endlesscode.inventory.internal.listener.SwapSlotContent
import ru.endlesscode.inventory.internal.listener.TakeSlotContent
import ru.endlesscode.inventory.internal.util.AIR
import ru.endlesscode.inventory.slot.ContainerInventorySlot
import ru.endlesscode.inventory.slot.ContainerSlot
import ru.endlesscode.inventory.slot.EmptyGuiSlot
import ru.endlesscode.inventory.slot.TestItemValidator
import ru.endlesscode.inventory.test.TestInventoryClickEvent
import ru.endlesscode.inventory.test.TestInventoryView
import ru.endlesscode.inventory.test.mockItemFactory
import java.util.*

class SlotInteractionsTest : FeatureSpec({

    val slotContentValidator = TestItemValidator()
    val inventoryLayout = InventoryLayoutImpl(
        id = "test",
        displayName = "Test",
        defaultSlot = EmptyGuiSlot,
        slotsMap = sortedMapOf(
            1 to ContainerSlot(
                texture = Material.BLACK_STAINED_GLASS_PANE,
                maxStackSize = 4,
                contentValidator = slotContentValidator,
            )
        ),
    )

    // SUT
    val inventory = spyk(CustomInventory(UUID.randomUUID(), inventoryLayout, mockk(), InstantTaskScheduler()))
    val slot = inventory.getSlotAt(1) as ContainerInventorySlot

    val inventoryView = TestInventoryView()
    lateinit var event: TestInventoryClickEvent

    beforeSpec {
        mockItemFactory()
    }

    lateinit var initSlotContent: ItemStack
    var initEventCursor: ItemStack? = null
    var initEventCurrentItem: ItemStack? = null

    fun assertState(
        content: ItemStack = initSlotContent,
        cursor: ItemStack? = initEventCursor,
        currentItem: ItemStack? = initEventCurrentItem,
        isCancelled: Boolean = false,
    ) {
        assertSoftly {
            withClue("Unexpected content") { slot.content shouldBe content }
            withClue("Unexpected currentItem") { event.currentItem shouldBe currentItem }
            withClue("Unexpected cursor") { event.cursor shouldBe cursor }
            withClue("Event should${if (isCancelled) "n't" else ""} be cancelled") {
                event.isCancelled shouldBe isCancelled
            }
        }
    }

    feature("take item") {

        fun takeContent(
            content: ItemStack = AIR,
            action: InventoryAction = PICKUP_ALL,
        ) {
            event = TestInventoryClickEvent(inventoryView, action)
            slot.content = content
            event.currentItem = slot.getView(DI.placeholders, mockk())

            initSlotContent = content
            initEventCursor = AIR
            initEventCurrentItem = event.currentItem

            val interaction = TakeSlotContent.fromClick(event, slot)
            inventory.handleInteraction(interaction)
        }

        scenario("take item from empty slot") {
            takeContent()
            assertState(isCancelled = true)
        }

        scenario("take item from slot") {
            takeContent(ItemStack(Material.BLAZE_ROD, 2))
            assertState(content = AIR)
        }

        scenario("take half items from slot") {
            takeContent(ItemStack(Material.BLAZE_ROD, 3), action = PICKUP_HALF)
            assertState(content = ItemStack(Material.BLAZE_ROD, 1))
        }
    }

    feature("place item") {

        fun placeContent(
            cursor: ItemStack = ItemStack(Material.STICK),
            current: ItemStack = AIR,
            action: InventoryAction = SWAP_WITH_CURSOR,
        ) {
            event = TestInventoryClickEvent(inventoryView, action)
            slot.content = current
            @Suppress("DEPRECATION")
            event.cursor = cursor
            event.currentItem = slot.getView(DI.placeholders, mockk())

            initSlotContent = current
            initEventCursor = cursor
            initEventCurrentItem = current

            val interaction = PlaceSlotContent.fromClick(event, slot)
            inventory.handleInteraction(interaction)
        }

        scenario("place item to empty slot") {
            val cursor = ItemStack(Material.STICK)
            placeContent(cursor)

            assertState(content = cursor, cursor = AIR)
        }

        scenario("place single item") {
            val cursor = ItemStack(Material.STICK, slot.maxStackSize + 1)
            placeContent(cursor, action = PLACE_ONE)

            assertState(content = ItemStack(Material.STICK, 1), cursor = ItemStack(Material.STICK, slot.maxStackSize))
        }

        scenario("place more than max stack size to empty slot") {
            val cursor = ItemStack(Material.STICK, slot.maxStackSize + 1)
            placeContent(cursor)

            assertState(
                content = ItemStack(Material.STICK, slot.maxStackSize),
                cursor = ItemStack(Material.STICK, 1),
            )
        }

        scenario("place similar item to full slot") {
            val cursor = ItemStack(Material.STICK)
            val current = ItemStack(Material.STICK, slot.maxStackSize)
            placeContent(cursor, current = current)

            assertState(isCancelled = true)
        }

        scenario("place single similar item") {
            val cursor = ItemStack(Material.STICK, 3)
            val current = ItemStack(Material.STICK)
            placeContent(cursor, current, action = PLACE_ONE)

            assertState(content = ItemStack(Material.STICK, 2), cursor = ItemStack(Material.STICK, 2))
        }

        scenario("place similar item to slot") {
            val cursor = ItemStack(Material.STICK)
            val current = ItemStack(Material.STICK, slot.maxStackSize - 1)
            placeContent(cursor, current)

            assertState(content = ItemStack(Material.STICK, slot.maxStackSize), cursor = AIR)
        }

        scenario("place similar item to slot with overflow") {
            val cursor = ItemStack(Material.STICK, 2)
            val current = ItemStack(Material.STICK, slot.maxStackSize - 1)
            placeContent(cursor, current)

            assertState(
                content = ItemStack(Material.STICK, slot.maxStackSize),
                cursor = ItemStack(Material.STICK, 1),
            )
        }
    }

    feature("swap item") {

        fun swapContent(
            content: ItemStack = AIR,
            item: ItemStack = AIR,
        ) {
            event = TestInventoryClickEvent(inventoryView, HOTBAR_SWAP)
            slot.content = content
            event.currentItem = slot.getView(DI.placeholders, mockk())

            initSlotContent = content
            initEventCursor = AIR
            initEventCurrentItem = event.currentItem

            val interaction = SwapSlotContent(event, slot, item)
            inventory.handleInteraction(interaction)
        }

        scenario("swap empty slot with empty item") {
            swapContent(content = AIR, item = AIR)
            assertState(isCancelled = true)
        }

        scenario("swap slot with empty item") {
            swapContent(content = ItemStack(Material.STICK))
            assertState(content = AIR)
        }

        scenario("swap slot with empty item when validator exists") {
            slotContentValidator.predicate = { it.type == Material.STICK }
            swapContent(content = ItemStack(Material.STICK))
            assertState(content = AIR)
        }

        scenario("swap empty slot with item") {
            val item = ItemStack(Material.STICK, 2)
            swapContent(item = item)

            assertState(content = item, currentItem = AIR)
        }

        scenario("swap slot with item not fitting to slot") {
            swapContent(item = ItemStack(Material.STICK, slot.maxStackSize + 1))
            assertState(isCancelled = true)
        }

        scenario("swap slot content with cursor") {
            val cursor = ItemStack(Material.BLAZE_ROD, 3)
            swapContent(content = ItemStack(Material.STICK, 2), item = cursor)

            assertState(content = cursor)
        }

        scenario("swap slot content with item not fitting to slot") {
            swapContent(
                content = ItemStack(Material.BLAZE_ROD),
                item = ItemStack(Material.STICK, slot.maxStackSize + 1)
            )

            assertState(isCancelled = true)
        }

        scenario("swap slot content with not allowed item") {
            slotContentValidator.predicate = { it.type != Material.STICK }
            swapContent(
                content = ItemStack(Material.BLAZE_ROD),
                item = ItemStack(Material.STICK),
            )

            assertState(isCancelled = true)
        }
    }
})
