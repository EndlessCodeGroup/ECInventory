package ru.endlesscode.rpginventory.test

import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView

class TestInventoryView(
    private val topInventory: Inventory = Inventory(size = 54),
    private val bottomInventory: Inventory = Inventory(size = 36),
    private val type: InventoryType = InventoryType.CHEST,
) : InventoryView() {

    override fun getTopInventory(): Inventory = topInventory
    override fun getBottomInventory(): Inventory = bottomInventory
    override fun getType(): InventoryType = type
    override fun getTitle(): String = "TestInventoryView"

    override fun getPlayer(): HumanEntity = error("Not mocked.")
}
