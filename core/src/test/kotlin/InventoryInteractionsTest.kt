package ru.endlesscode.rpginventory

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.internal.InstantTaskScheduler
import ru.endlesscode.rpginventory.slot.AddItemToInventory
import ru.endlesscode.rpginventory.slot.ItemValidator
import ru.endlesscode.rpginventory.slot.Slot
import ru.endlesscode.rpginventory.slot.SlotImpl
import ru.endlesscode.rpginventory.test.TestInventoryClickEvent
import ru.endlesscode.rpginventory.test.TestInventoryView
import ru.endlesscode.rpginventory.test.mockItemFactory
import ru.endlesscode.rpginventory.util.AIR
import ru.endlesscode.rpginventory.util.orEmpty

class InventoryInteractionsTest : FeatureSpec({

    // SUT
    lateinit var inventory: CustomInventory

    val inventoryView = TestInventoryView()
    lateinit var event: TestInventoryClickEvent

    beforeSpec {
        mockItemFactory()
    }

    val maxStackSize = 4
    var slotsCounter = 0
    fun slot(): Slot = SlotImpl(
        id = "slot${++slotsCounter}",
        name = "Slot $slotsCounter",
        texture = AIR,
        type = Slot.Type.STORAGE,
        contentValidator = ItemValidator.any,
        maxStackSize = maxStackSize,
    )

    feature("add item to inventory") {

        fun initSlots(vararg slots: Int) {
            val inventoryLayout = InventoryLayoutImpl(
                name = "test",
                emptySlotTexture = AIR,
                slotsMap = slots.associateWith { slot() }.toSortedMap(),
            )
            inventory = spyk(CustomInventory(inventoryLayout, InstantTaskScheduler()))
        }

        fun addItemToInventory(item: ItemStack) {
            event = TestInventoryClickEvent(inventoryView, MOVE_TO_OTHER_INVENTORY, slot = inventory.viewSize + 1)
            event.currentItem = item
            val interaction = AddItemToInventory(event, item)
            inventory.handleInteraction(interaction)
        }

        fun assertSlots(current: ItemStack, vararg pairs: Pair<Int, ItemStack>) {
            val expectedContent = pairs.toMap()
            inventory.getSlots().forAll { slot ->
                slot.content shouldBe expectedContent[slot.position].orEmpty()
            }
            event.currentItem shouldBe current
        }

        scenario("add item to empty slot") {
            initSlots(1)
            val item = ItemStack(Material.STICK, 2)
            addItemToInventory(item)

            assertSlots(current = AIR,1 to item)
        }

        scenario("add item to two slots") {
            initSlots(1, 42)
            val item = ItemStack(Material.STICK, 6)
            addItemToInventory(item)

            assertSlots(
                current = AIR,
                1 to ItemStack(Material.STICK, 4),
                42 to ItemStack(Material.STICK, 2)
            )
        }

        scenario("add item to similar slot") {
            initSlots(1)
            inventory.findEmptySlot()?.content = ItemStack(Material.STICK, 2)
            val item = ItemStack(Material.STICK, 3)
            addItemToInventory(item)

            assertSlots(
                current = ItemStack(Material.STICK, 1),
                1 to ItemStack(Material.STICK, 4),
            )
        }

        scenario("there no empty slots") {
            initSlots(1)
            inventory.findEmptySlot()?.content = ItemStack(Material.STICK)
            val item = ItemStack(Material.BLAZE_ROD)
            addItemToInventory(item)

            assertSlots(
                current = item,
                1 to ItemStack(Material.STICK),
            )
        }
    }
})
