package ru.endlesscode.rpginventory.test

import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.util.AIR

class TestInventoryView(
    private val topInventory: Inventory = Inventory(size = 54),
    private val bottomInventory: Inventory = Inventory(size = 36),
    private val type: InventoryType = InventoryType.CHEST,
) : InventoryView() {

    private var _cursor: ItemStack = AIR

    private val player = mockk<Player>(relaxUnitFun = true) {
        every { itemOnCursor } answers { _cursor }
        every { setItemOnCursor(any()) } answers { _cursor = firstArg() }
    }

    override fun getTopInventory(): Inventory = topInventory
    override fun getBottomInventory(): Inventory = bottomInventory
    override fun getType(): InventoryType = type
    override fun getTitle(): String = "TestInventoryView"
    override fun getPlayer(): HumanEntity = player
}
