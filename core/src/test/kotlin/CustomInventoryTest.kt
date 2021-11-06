package ru.endlesscode.rpginventory

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.internal.InstantTaskScheduler
import ru.endlesscode.rpginventory.slot.*
import ru.endlesscode.rpginventory.test.TestInventoryClickEvent
import ru.endlesscode.rpginventory.test.mockItemFactory
import ru.endlesscode.rpginventory.util.AIR

class CustomInventoryTest : FeatureSpec({

    val inventoryLayout = InventoryLayoutImpl(
        name = "test",
        emptySlotTexture = AIR,
        slotsMap = sortedMapOf(
            1 to SlotImpl(
                id = "test-slot",
                name = "Test Slot",
                texture = ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                type = Slot.Type.STORAGE,
                contentValidator = ItemValidator.any,
                maxStackSize = 1,
            )
        )
    )

    // SUT
    val inventory = spyk(CustomInventory(inventoryLayout, InstantTaskScheduler()))
    val slot = inventory.getSlot(0)

    beforeSpec {
        mockItemFactory()
    }

    feature("inventory interaction handling") {
        val event = TestInventoryClickEvent()

        fun TestInventoryClickEvent.mockCursor(item: ItemStack = ItemStack(Material.STICK)): ItemStack {
            cursor = item
            return item
        }

        scenario("take content from empty slot") {
            val interaction = TakeSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            event.isCancelled.shouldBeTrue()
        }

        scenario("take content from slot") {
            slot.content = ItemStack(Material.BLAZE_ROD)
            val interaction = TakeSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            verify { inventory.syncSlotWithView(slot) }
        }

        scenario("place item to empty slot") {
            val item = event.mockCursor()
            val interaction = PlaceSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe item
                event.currentItem shouldBe AIR
            }
        }

        scenario("swap slot content with cursor") {
            val item = event.mockCursor()
            slot.content = ItemStack(Material.BLAZE_ROD)
            val interaction = PlaceSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            slot.content shouldBe item
        }
    }
})
