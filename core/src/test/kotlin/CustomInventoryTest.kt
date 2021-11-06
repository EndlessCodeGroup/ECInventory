package ru.endlesscode.rpginventory

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.internal.InstantTaskScheduler
import ru.endlesscode.rpginventory.slot.*
import ru.endlesscode.rpginventory.test.TestInventoryClickEvent
import ru.endlesscode.rpginventory.test.TestInventoryView
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
                maxStackSize = 4,
            )
        )
    )

    // SUT
    val inventory = spyk(CustomInventory(inventoryLayout, InstantTaskScheduler()))
    val slot = inventory.getSlot(0)

    val inventoryView = TestInventoryView()
    lateinit var event: TestInventoryClickEvent

    beforeSpec {
        mockItemFactory()
    }

    feature("take item") {

        fun takeContent(content: ItemStack = AIR): TakeSlotContent {
            event = TestInventoryClickEvent(inventoryView)
            slot.content = content
            return TakeSlotContent(event, slot)
        }

        scenario("take content from empty slot") {
            val interaction = takeContent()
            inventory.handleInteraction(interaction)

            event.isCancelled.shouldBeTrue()
        }

        scenario("take content from slot") {
            val interaction = takeContent(ItemStack(Material.BLAZE_ROD))
            inventory.handleInteraction(interaction)

            verify { inventory.syncSlotWithView(slot) }
        }
    }

    feature("place item") {

        fun placeContent(
            item: ItemStack = ItemStack(Material.STICK),
            current: ItemStack = AIR,
            action: InventoryAction = InventoryAction.PLACE_ALL,
        ): PlaceSlotContent {
            event = TestInventoryClickEvent(inventoryView, action)
            slot.content = current
            event.cursor = item
            return PlaceSlotContent(event, slot)
        }

        scenario("place item to empty slot") {
            val interaction = placeContent()
            val item = interaction.item
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe item
                event.currentItem shouldBe AIR
            }
        }

        scenario("place single item") {
            val stack = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val interaction = placeContent(stack, action = InventoryAction.PLACE_ONE)
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe ItemStack(Material.STICK, 1)
            }
        }

        scenario("place more than max stack size to empty slot") {
            val item = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val interaction = placeContent(item)
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe ItemStack(Material.STICK, slot.maxStackSize)
                event.cursor shouldBe ItemStack(Material.STICK, 1)
            }
        }

        scenario("place similar item to full slot") {
            val item = ItemStack(Material.STICK)
            val currentContent = ItemStack(Material.STICK, slot.maxStackSize)
            val interaction = placeContent(item, current = currentContent)
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe currentContent
                event.isCancelled.shouldBeTrue()
            }
        }

        scenario("place single similar item") {
            val stack = ItemStack(Material.STICK, slot.maxStackSize)
            val interaction = placeContent(
                stack,
                current = ItemStack(Material.STICK),
                action = InventoryAction.PLACE_ONE,
            )
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe ItemStack(Material.STICK, 2)
            }
        }

        scenario("place similar item to slot") {
            val item = ItemStack(Material.STICK)
            val interaction = placeContent(item, current = ItemStack(Material.STICK, slot.maxStackSize - 1))
            inventory.handleInteraction(interaction)

            slot.content shouldBe ItemStack(Material.STICK, slot.maxStackSize)
        }

        scenario("place similar item to slot with overflow") {
            val item = ItemStack(Material.STICK, 2)
            val interaction = placeContent(item, current = ItemStack(Material.STICK, slot.maxStackSize - 1))
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe ItemStack(Material.STICK, slot.maxStackSize)
                event.cursor shouldBe ItemStack(Material.STICK, 1)
            }
        }

        scenario("replace item in slot") {
            val newItem = ItemStack(Material.BLAZE_ROD, 3)
            val currentItem = ItemStack(Material.STICK, 2)
            val interaction = placeContent(newItem, current = currentItem)
            inventory.handleInteraction(interaction)

            slot.content shouldBe newItem
        }

        scenario("try to replace item with item not fitting to slot") {
            val largeStack = ItemStack(Material.STICK, slot.maxStackSize + 1)
            val currentItem = ItemStack(Material.BLAZE_ROD)
            val interaction = placeContent(largeStack, current = currentItem)
            inventory.handleInteraction(interaction)

            assertSoftly {
                slot.content shouldBe currentItem
                event.isCancelled.shouldBeTrue()
            }
        }
    }
})
