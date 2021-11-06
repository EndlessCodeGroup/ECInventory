package ru.endlesscode.rpginventory.listener

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.slot.InventorySlot
import ru.endlesscode.rpginventory.slot.PlaceSlotContent
import ru.endlesscode.rpginventory.slot.SlotInteraction
import ru.endlesscode.rpginventory.slot.TakeSlotContent
import ru.endlesscode.rpginventory.test.Inventory
import ru.endlesscode.rpginventory.test.TestInventoryClickEvent
import ru.endlesscode.rpginventory.test.TestInventoryView
import ru.endlesscode.rpginventory.test.mockItemFactory

class InventoryClicksRouterTest : FeatureSpec({

    // SUT
    val router = InventoryClicksRouter()

    var clickedSlot: InventorySlot? = null
    val inventory = mockk<CustomInventory>(relaxUnitFun = true) {
        every { viewSize } returns 54
        every { getSlotAt(any()) } answers { clickedSlot }
    }
    val inventoryView = TestInventoryView(topInventory = Inventory(inventory))

    beforeSpec {
        mockItemFactory()
    }

    fun clickEvent(
        action: InventoryAction = InventoryAction.NOTHING,
        slot: Int = 0,
    ) = TestInventoryClickEvent(inventoryView, action, slot)

    feature("inventory click event") {
        lateinit var event: TestInventoryClickEvent

        fun verifyInteraction(
            interaction: SlotInteraction?,
            eventCancelled: Boolean = false,
        ) {
            assertSoftly {
                event.isCancelled shouldBe eventCancelled
                if (interaction != null) {
                    verify { inventory.handleInteraction(interaction) }
                } else {
                    verify(exactly = 0) { inventory.handleInteraction(any()) }
                }
            }
        }

        scenario("click outside") {
            event = clickEvent(slot = InventoryView.OUTSIDE)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("click non-functional inventory slot") {
            event = clickEvent(InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("do nothing with slot") {
            clickedSlot = mockk()
            event = clickEvent(InventoryAction.NOTHING)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("take item from slot") {
            clickedSlot = mockk()
            event = clickEvent(InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(TakeSlotContent(event, clickedSlot!!))
        }

        scenario("place item to slot") {
            clickedSlot = mockk()
            event = clickEvent(InventoryAction.PLACE_ALL)
            event.cursor = ItemStack(Material.STICK)
            router.onClick(event)

            verifyInteraction(PlaceSlotContent(event, clickedSlot!!))
        }
    }
})
