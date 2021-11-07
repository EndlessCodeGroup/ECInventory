package ru.endlesscode.rpginventory.listener

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.ClickType.SWAP_OFFHAND
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryAction.COLLECT_TO_CURSOR
import org.bukkit.event.inventory.InventoryAction.HOTBAR_SWAP
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.slot.*
import ru.endlesscode.rpginventory.test.*
import ru.endlesscode.rpginventory.util.AIR

class InventoryClicksRouterTest : FeatureSpec({

    // SUT
    val router = InventoryClicksRouter()

    var clickedSlot: InventorySlot? = null
    var appliedInteraction: SlotInteraction? = null
    val inventory = mockk<CustomInventory>(relaxUnitFun = true) {
        every { viewSize } returns 54
        every { getSlotAt(any()) } answers { clickedSlot }
        every { handleInteraction(any()) } answers { appliedInteraction = firstArg() }
    }
    val inventoryView = TestInventoryView(Inventory(inventory))

    lateinit var interactEvent: InventoryInteractEvent

    beforeSpec {
        mockItemFactory()
    }

    fun verifyInteraction(
        interaction: SlotInteraction?,
        eventCancelled: Boolean = false,
    ) {
        assertSoftly {
            interactEvent.isCancelled shouldBe eventCancelled
            appliedInteraction shouldBe interaction
        }
    }

    feature("inventory click event") {
        fun clickEvent(
            action: InventoryAction = InventoryAction.NOTHING,
            slot: Int = 0,
            click: ClickType = ClickType.LEFT,
            hotbarKey: Int = -1,
        ) = TestInventoryClickEvent(inventoryView, action, slot, click = click, hotbarKey = hotbarKey)
            .also { interactEvent = it }

        scenario("click outside") {
            val event = clickEvent(slot = InventoryView.OUTSIDE)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("click non-functional inventory slot") {
            val event = clickEvent(InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("do nothing with slot") {
            clickedSlot = mockk()
            val event = clickEvent(InventoryAction.NOTHING)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("take item from slot") {
            clickedSlot = mockk {
                every { content } returns ItemStack(Material.STICK)
            }
            val event = clickEvent(InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(TakeSlotContent.fromClick(event, clickedSlot!!))
        }

        scenario("place item to slot") {
            clickedSlot = mockk()
            val event = clickEvent(InventoryAction.PLACE_ALL)
            event.cursor = ItemStack(Material.STICK)
            router.onClick(event)

            verifyInteraction(PlaceSlotContent.fromClick(event, clickedSlot!!))
        }

        scenario("collect items similar to inventory items") {
            every { inventoryView.topInventory.contents } returns arrayOf(ItemStack(Material.STICK, 2))
            inventoryView.cursor = ItemStack(Material.STICK)
            val event = clickEvent(COLLECT_TO_CURSOR, slot = inventory.viewSize + 1)
            router.onClick(event)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("collect items not similar to inventory items") {
            every { inventoryView.topInventory.contents } returns arrayOf(ItemStack(Material.BLAZE_ROD, 2))
            inventoryView.cursor = ItemStack(Material.STICK)
            val event = clickEvent(COLLECT_TO_CURSOR, slot = inventory.viewSize + 1)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("collect items inside inventory") {
            val event = clickEvent(COLLECT_TO_CURSOR)
            router.onClick(event)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("swap with hotbar slot") {
            clickedSlot = mockk { every { content } returns AIR }
            val hotbarItem = ItemStack(Material.STICK)
            inventoryView.bottomInventory.setItem(0, hotbarItem)
            val event = clickEvent(HOTBAR_SWAP, hotbarKey = 0)
            router.onClick(event)

            verifyInteraction(interaction = HotbarSwapSlotContent(event, clickedSlot!!, hotbarItem))
        }

        scenario("swap with offhand slot") {
            clickedSlot = mockk { every { content } returns AIR }
            val offhandItem = ItemStack(Material.STICK)
            inventoryView.offhandItem = offhandItem
            val event = clickEvent(HOTBAR_SWAP, click = SWAP_OFFHAND)
            router.onClick(event)

            verifyInteraction(interaction = HotbarSwapSlotContent(event, clickedSlot!!, offhandItem))
        }
    }

    feature("inventory drag event") {

        fun dragEvent(
            cursor: ItemStack = ItemStack(Material.STICK),
            rightClick: Boolean = false,
            slots: Map<Int, ItemStack> = mapOf(0 to cursor),
        ) = TestInventoryDragEvent(inventoryView, cursor, rightClick = rightClick, slots = slots)
            .also { interactEvent = it }

        scenario("click non-functional inventory slot") {
            router.onDrag(dragEvent())

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("drag in inventory") {
            val slots = mapOf(
                0 to ItemStack(Material.STICK),
                inventory.viewSize to ItemStack(Material.STICK),
            )
            router.onDrag(dragEvent(slots = slots))

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("drag outside of inventory") {
            val slots = mapOf(
                inventory.viewSize to ItemStack(Material.STICK),
                inventory.viewSize + 1 to ItemStack(Material.STICK),
            )
            router.onDrag(dragEvent(slots = slots))

            verifyInteraction(interaction = null)
        }

        scenario("place all items") {
            clickedSlot = mockk()
            val item = ItemStack(Material.STICK, 2)
            val event = dragEvent(item)
            router.onDrag(event)

            verifyInteraction(PlaceSlotContent(event, clickedSlot!!, item, amount = 2))
        }

        scenario("place one item") {
            clickedSlot = mockk()
            val item = ItemStack(Material.STICK, 2)
            val event = dragEvent(item, rightClick = true)
            router.onDrag(event)

            verifyInteraction(PlaceSlotContent(event, clickedSlot!!, item, amount = 1))
        }
    }
})
