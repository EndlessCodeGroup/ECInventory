package ru.endlesscode.rpginventory.listener

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.slot.InventorySlot
import ru.endlesscode.rpginventory.slot.PlaceSlotContent
import ru.endlesscode.rpginventory.slot.SlotInteraction
import ru.endlesscode.rpginventory.slot.TakeSlotContent
import ru.endlesscode.rpginventory.test.*

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
    val inventoryView = TestInventoryView(topInventory = Inventory(inventory))

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
        ) = TestInventoryClickEvent(inventoryView, action, slot).also { interactEvent = it }

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
    }

    feature("inventory drag event") {

        fun dragEvent(
            cursor: ItemStack = ItemStack(Material.STICK),
            slots: Map<Int, ItemStack> = mapOf(0 to cursor)
        ) = TestInventoryDragEvent(inventoryView, cursor, slots = slots).also { interactEvent = it }

        scenario("click non-functional inventory slot") {
            router.onDrag(dragEvent())

            verifyInteraction(interaction = null, eventCancelled = true)
        }
    }
})
