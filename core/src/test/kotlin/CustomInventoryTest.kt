package ru.endlesscode.rpginventory

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.slot.*
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
    val inventory = CustomInventory(inventoryLayout)
    val slot = inventory.getSlot(0)

    beforeSpec {
        mockItemFactory()
    }

    feature("inventory interaction handling") {
        val event = mockk<InventoryClickEvent>(relaxUnitFun = true)

        scenario("take content from empty slot") {
            val interaction = TakeSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            verify { interaction.cancel() }
        }

        scenario("place item to empty slot") {
            val item = ItemStack(Material.STICK)
            every { event.cursor } returns ItemStack(Material.STICK)
            val interaction = PlaceSlotContent(event, slot)
            inventory.handleInteraction(interaction)

            slot.content shouldBe item
        }
    }
})
