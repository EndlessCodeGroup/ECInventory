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

package ru.endlesscode.inventory.internal.listener

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.ClickType.LEFT
import org.bukkit.event.inventory.ClickType.SWAP_OFFHAND
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.InstantTaskScheduler
import ru.endlesscode.inventory.internal.util.AIR
import ru.endlesscode.inventory.internal.util.isEmpty
import ru.endlesscode.inventory.slot.ContainerInventorySlot
import ru.endlesscode.inventory.slot.GuiInventorySlot
import ru.endlesscode.inventory.slot.InventorySlot
import ru.endlesscode.inventory.test.*

class InventoryClicksRouterTest : FeatureSpec({

    // SUT
    val router = InventoryClicksRouter(InstantTaskScheduler())

    var clickedSlot: InventorySlot = mockk<GuiInventorySlot>()
    var appliedInteraction: InventoryInteraction? = null
    val inventory = mockk<CustomInventory>(relaxUnitFun = true) {
        every { size } returns 54
        every { getSlotAt(any()) } answers { clickedSlot }
        every { handleInteraction(any()) } answers { appliedInteraction = firstArg() }
    }
    val inventoryView = TestInventoryView(Inventory(inventory))

    lateinit var interactEvent: InventoryInteractEvent

    beforeSpec {
        mockItemFactory()
    }

    fun verifyInteraction(
        interaction: InventoryInteraction?,
        eventCancelled: Boolean = false,
    ) {
        assertSoftly {
            interactEvent.isCancelled shouldBe eventCancelled
            appliedInteraction shouldBe interaction
        }
    }

    fun clickedContainerSlot(content: ItemStack = AIR): ContainerInventorySlot {
        val slot = mockk<ContainerInventorySlot> {
            every { this@mockk.content } returns content
            every { isEmpty() } returns content.isEmpty()
        }
        clickedSlot = slot
        return slot
    }

    feature("inventory click event") {
        fun performClick(
            action: InventoryAction = NOTHING,
            slot: Int = 0,
            click: ClickType = LEFT,
            hotbarKey: Int = -1,
            current: ItemStack = AIR,
        ) = TestInventoryClickEvent(inventoryView, action, slot, click = click, hotbarKey = hotbarKey).also { event ->
            event.currentItem = current
            interactEvent = event
            router.onClick(event)
        }

        scenario("click outside") {
            performClick(slot = InventoryView.OUTSIDE)
            verifyInteraction(interaction = null)
        }

        scenario("click gui inventory slot") {
            performClick(PICKUP_ALL)
            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("do nothing with slot") {
            clickedContainerSlot()
            performClick(NOTHING)

            verifyInteraction(interaction = null)
        }

        scenario("take item from slot") {
            val slot = clickedContainerSlot(ItemStack(Material.STICK))
            val event = performClick(PICKUP_ALL)

            verifyInteraction(TakeSlotContent.fromClick(event, slot))
        }

        scenario("place item to slot") {
            val slot = clickedContainerSlot()
            inventoryView.cursor = ItemStack(Material.STICK)
            val event = performClick(PLACE_ALL)

            verifyInteraction(PlaceSlotContent.fromClick(event, slot))
        }

        scenario("collect items similar to inventory items") {
            every { inventoryView.topInventory.contents } returns arrayOf(ItemStack(Material.STICK, 2))
            inventoryView.cursor = ItemStack(Material.STICK)
            performClick(COLLECT_TO_CURSOR, slot = inventory.size + 1)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("collect items not similar to inventory items") {
            every { inventoryView.topInventory.contents } returns arrayOf(ItemStack(Material.BLAZE_ROD, 2))
            inventoryView.cursor = ItemStack(Material.STICK)
            performClick(COLLECT_TO_CURSOR, slot = inventory.size + 1)

            verifyInteraction(interaction = null)
        }

        scenario("collect items inside inventory") {
            clickedContainerSlot()
            performClick(COLLECT_TO_CURSOR)
            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("swap with cursor empty slot") {
            val slot = clickedContainerSlot()
            val event = performClick(SWAP_WITH_CURSOR)

            verifyInteraction(PlaceSlotContent.fromClick(event, slot))
        }

        scenario("swap with cursor non-empty item") {
            val slot = clickedContainerSlot(ItemStack(Material.STICK))
            val event = performClick(SWAP_WITH_CURSOR)

            verifyInteraction(SwapSlotContent.fromClick(event, slot))
        }

        scenario("swap with hotbar slot") {
            val slot = clickedContainerSlot()
            val hotbarItem = ItemStack(Material.STICK)
            inventoryView.bottomInventory.setItem(0, hotbarItem)
            val event = performClick(HOTBAR_SWAP, hotbarKey = 0)

            verifyInteraction(SwapSlotContent(event, slot, hotbarItem))
        }

        scenario("swap with offhand slot") {
            val slot = clickedContainerSlot()
            val offhandItem = ItemStack(Material.STICK)
            inventoryView.offhandItem = offhandItem
            val event = performClick(HOTBAR_SWAP, click = SWAP_OFFHAND)

            verifyInteraction(SwapSlotContent(event, slot, offhandItem))
        }

        scenario("move empty item to inventory") {
            performClick(MOVE_TO_OTHER_INVENTORY, slot = inventory.size + 1)
            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("move item to inventory") {
            val item = ItemStack(Material.STICK)
            val event = performClick(MOVE_TO_OTHER_INVENTORY, current = item, slot = inventory.size + 1)

            verifyInteraction(AddItemToInventory(event, item), eventCancelled = true)
        }
    }

    feature("inventory drag event") {

        fun performDrag(
            cursor: ItemStack = ItemStack(Material.STICK),
            rightClick: Boolean = false,
            slots: Map<Int, ItemStack> = mapOf(0 to cursor),
        ) = TestInventoryDragEvent(inventoryView, cursor, rightClick = rightClick, slots = slots).also { event ->
            interactEvent = event
            router.onDrag(event)
        }

        scenario("drag gui inventory slot") {
            performDrag()
            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("drag in inventory") {
            performDrag(
                slots = mapOf(
                    0 to ItemStack(Material.STICK),
                    inventory.size to ItemStack(Material.STICK),
                ),
            )

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("drag outside of inventory") {
            performDrag(
                slots = mapOf(
                    inventory.size to ItemStack(Material.STICK),
                    inventory.size + 1 to ItemStack(Material.STICK),
                ),
            )

            verifyInteraction(interaction = null)
        }

        scenario("place all items") {
            val slot = clickedContainerSlot()
            val item = ItemStack(Material.STICK, 2)
            val event = performDrag(item)

            verifyInteraction(PlaceSlotContent(event, slot, item, amount = 2))
        }

        scenario("place one item") {
            val slot = clickedContainerSlot()
            val item = ItemStack(Material.STICK, 2)
            val event = performDrag(item, rightClick = true)

            verifyInteraction(PlaceSlotContent(event, slot, item, amount = 1))
        }
    }
})
