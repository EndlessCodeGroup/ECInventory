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

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.InstantTaskScheduler
import ru.endlesscode.inventory.internal.listener.HotbarSwapSlotContent
import ru.endlesscode.inventory.internal.listener.PlaceSlotContent
import ru.endlesscode.inventory.internal.listener.TakeSlotContent
import ru.endlesscode.inventory.internal.util.AIR
import ru.endlesscode.inventory.slot.ItemValidator
import ru.endlesscode.inventory.slot.Slot
import ru.endlesscode.inventory.slot.SlotImpl
import ru.endlesscode.inventory.test.TestInventoryClickEvent
import ru.endlesscode.inventory.test.TestInventoryView
import ru.endlesscode.inventory.test.mockItemFactory
import java.util.*

class SlotInteractionsTest : FeatureSpec({

    val inventoryLayout = InventoryLayoutImpl(
        id = "test",
        name = "Test",
        emptySlotTexture = AIR,
        slotsMap = sortedMapOf(
            1 to SlotImpl(
                id = "test-slot",
                name = "Test Slot",
                texture = ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                type = Slot.Type.STORAGE,
                contentValidator = ItemValidator.any,
                maxStackSize = 4,
            )
        )
    )

    // SUT
    val inventory = spyk(CustomInventory(UUID.randomUUID(), inventoryLayout, InstantTaskScheduler()))
    val slot = inventory.getSlot(0)

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
        ): TakeSlotContent {
            event = TestInventoryClickEvent(inventoryView, action)
            slot.content = content
            event.currentItem = slot.getContentOrTexture()

            initSlotContent = content
            initEventCursor = AIR
            initEventCurrentItem = event.currentItem

            return TakeSlotContent.fromClick(event, slot)
        }

        scenario("take item from empty slot") {
            val interaction = takeContent()
            inventory.handleInteraction(interaction)

            assertState(isCancelled = true)
        }

        scenario("take item from slot") {
            val interaction = takeContent(ItemStack(Material.BLAZE_ROD))
            inventory.handleInteraction(interaction)

            assertState(content = AIR)
        }

        scenario("take half items from slot") {
            val interaction = takeContent(ItemStack(Material.BLAZE_ROD, 3), action = PICKUP_HALF)
            inventory.handleInteraction(interaction)

            assertState(content = ItemStack(Material.BLAZE_ROD, 1))
        }
    }

    feature("place item") {

        fun placeContent(
            cursor: ItemStack = ItemStack(Material.STICK),
            current: ItemStack = AIR,
            action: InventoryAction = SWAP_WITH_CURSOR,
        ): PlaceSlotContent {
            event = TestInventoryClickEvent(inventoryView, action)
            slot.content = current
            event.cursor = cursor
            event.currentItem = slot.getContentOrTexture()

            initSlotContent = current
            initEventCursor = cursor
            initEventCurrentItem = current

            return PlaceSlotContent.fromClick(event, slot)
        }

        scenario("place item to empty slot") {
            val cursor = ItemStack(Material.STICK)
            val interaction = placeContent(cursor)
            inventory.handleInteraction(interaction)

            assertState(content = cursor)
        }

        scenario("place single item") {
            val cursor = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val interaction = placeContent(cursor, action = PLACE_ONE)
            inventory.handleInteraction(interaction)

            assertState(content = ItemStack(Material.STICK, 1))
        }

        scenario("place more than max stack size to empty slot") {
            val cursor = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val interaction = placeContent(cursor)
            inventory.handleInteraction(interaction)

            assertState(
                content = ItemStack(Material.STICK, slot.maxStackSize),
                cursor = ItemStack(Material.STICK, 1),
            )
        }

        scenario("place similar item to full slot") {
            val cursor = ItemStack(Material.STICK)
            val current = ItemStack(Material.STICK, slot.maxStackSize)
            val interaction = placeContent(cursor, current = current)
            inventory.handleInteraction(interaction)

            assertState(isCancelled = true)
        }

        scenario("place single similar item") {
            val cursor = ItemStack(Material.STICK, slot.maxStackSize)
            val current = ItemStack(Material.STICK)
            val interaction = placeContent(cursor, current, action = PLACE_ONE)
            inventory.handleInteraction(interaction)

            assertState(content = ItemStack(Material.STICK, 2))
        }

        scenario("place similar item to slot") {
            val cursor = ItemStack(Material.STICK)
            val current = ItemStack(Material.STICK, slot.maxStackSize - 1)
            val interaction = placeContent(cursor, current)
            inventory.handleInteraction(interaction)

            assertState(content = ItemStack(Material.STICK, slot.maxStackSize))
        }

        scenario("place similar item to slot with overflow") {
            val cursor = ItemStack(Material.STICK, 2)
            val current = ItemStack(Material.STICK, slot.maxStackSize - 1)
            val interaction = placeContent(cursor, current)
            inventory.handleInteraction(interaction)

            assertState(
                content = ItemStack(Material.STICK, slot.maxStackSize),
                cursor = ItemStack(Material.STICK, 1),
            )
        }

        scenario("replace item in slot") {
            val cursor = ItemStack(Material.BLAZE_ROD, 3)
            val interaction = placeContent(cursor, ItemStack(Material.STICK, 2))
            inventory.handleInteraction(interaction)

            assertState(content = cursor)
        }

        scenario("try to replace item with item not fitting to slot") {
            val largeStack = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val currentItem = ItemStack(Material.BLAZE_ROD)
            val interaction = placeContent(largeStack, current = currentItem)
            inventory.handleInteraction(interaction)

            assertState(isCancelled = true)
        }
    }

    feature("swap with hotbar") {

        fun hotbarSwap(
            content: ItemStack = AIR,
            hotbarItem: ItemStack = AIR,
        ) {
            event = TestInventoryClickEvent(inventoryView, HOTBAR_SWAP)
            slot.content = content
            event.currentItem = slot.getContentOrTexture()

            initSlotContent = content
            initEventCursor = AIR
            initEventCurrentItem = event.currentItem

            val interaction = HotbarSwapSlotContent(event, slot, hotbarItem)
            inventory.handleInteraction(interaction)
        }

        scenario("swap empty slot with empty hotbar") {
            hotbarSwap(content = AIR, hotbarItem = AIR)

            assertState(isCancelled = true)
        }

        scenario("swap slot with empty hotbar") {
            hotbarSwap(content = ItemStack(Material.STICK))

            assertState(content = AIR)
        }

        scenario("swap empty slot with hotbar") {
            val hotbarItem = ItemStack(Material.STICK, 2)
            hotbarSwap(hotbarItem = hotbarItem)

            assertState(content = hotbarItem, currentItem = AIR)
        }

        scenario("swap slot with item not fitting to slot") {
            val hotbarItem = ItemStack(Material.STICK, slot.maxStackSize + 1)
            hotbarSwap(hotbarItem = hotbarItem)

            assertState(isCancelled = true)
        }
    }
})
