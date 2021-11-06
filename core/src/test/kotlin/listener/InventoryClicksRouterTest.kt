package ru.endlesscode.rpginventory.listener

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.mockItemFactory
import ru.endlesscode.rpginventory.slot.InventorySlot
import ru.endlesscode.rpginventory.slot.PlaceSlotContent
import ru.endlesscode.rpginventory.slot.SlotInteraction
import ru.endlesscode.rpginventory.slot.TakeSlotContent
import ru.endlesscode.rpginventory.util.AIR

class InventoryClicksRouterTest : FeatureSpec({

    // SUT
    val router = InventoryClicksRouter()

    var clickedSlot: InventorySlot? = null
    val inventory = mockk<CustomInventory>(relaxUnitFun = true) {
        every { getSlotAt(any()) } answers { clickedSlot }
    }
    mockItemFactory()

    fun mockClickEvent(
        rawSlot: Int,
        action: InventoryAction,
        slot: Int = rawSlot,
        slotType: InventoryType.SlotType = InventoryType.SlotType.CONTAINER,
        click: ClickType = ClickType.LEFT,
        cursor: ItemStack = AIR,
    ): InventoryClickEvent {
        val topInventory = mockk<Inventory> {
            every { holder } returns inventory
        }
        val view = mockk<InventoryView> {
            every { getTopInventory() } returns topInventory
            every { getInventory(rawSlot) } returns if (rawSlot < 0) null else topInventory
            every { convertSlot(rawSlot) } returns slot
            every { getCursor() } returns cursor
        }

        return InventoryClickEvent(view, slotType, rawSlot, click, action)
    }

    feature("inventory click event") {
        lateinit var event: InventoryClickEvent

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
            event = mockClickEvent(InventoryView.OUTSIDE, InventoryAction.NOTHING)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("click non-functional inventory slot") {
            event = mockClickEvent(rawSlot = 42, InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(interaction = null, eventCancelled = true)
        }

        scenario("do nothing with slot") {
            clickedSlot = mockk()
            event = mockClickEvent(rawSlot = 42, InventoryAction.NOTHING)
            router.onClick(event)

            verifyInteraction(interaction = null)
        }

        scenario("take item from slot") {
            clickedSlot = mockk()
            event = mockClickEvent(rawSlot = 42, InventoryAction.PICKUP_ALL)
            router.onClick(event)

            verifyInteraction(TakeSlotContent(event, clickedSlot!!))
        }

        scenario("place item to slot") {
            clickedSlot = mockk()
            event = mockClickEvent(rawSlot = 42, InventoryAction.PLACE_ALL, cursor = ItemStack(Material.STICK))
            router.onClick(event)

            verifyInteraction(PlaceSlotContent(event, clickedSlot!!))
        }
    }
})
