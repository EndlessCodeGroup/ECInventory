package ru.endlesscode.rpginventory

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryAction.*
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

    lateinit var initSlotContent: ItemStack
    var initEventCursor: ItemStack? = null
    var initEventCurrentItem: ItemStack? = null

    fun assertState(
        content: ItemStack = initSlotContent,
        cursor: ItemStack? = initEventCursor,
        currentItem: ItemStack? = initEventCurrentItem,
        isCancelled: Boolean = false,
        syncSlot: Boolean = false,
    ) {
        assertSoftly {
            withClue("Unexpected content") { slot.content shouldBe content }
            withClue("Unexpected currentItem") { event.currentItem shouldBe currentItem }
            withClue("Unexpected cursor") { event.cursor shouldBe cursor }
            withClue("Event should${if (isCancelled) "n't" else ""} be cancelled") {
                event.isCancelled shouldBe isCancelled
            }
            verify(exactly = if (syncSlot) 1 else 0) { inventory.syncSlotWithView(slot) }
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

            assertState(
                content = AIR,
                syncSlot = true,
            )
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
                syncSlot = true,
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
                syncSlot = true,
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
})
