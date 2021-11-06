package ru.endlesscode.rpginventory.test

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.util.AIR

class TestInventoryClickEvent(
    view: InventoryView = TestInventoryView(),
    action: InventoryAction = InventoryAction.NOTHING,
    slot: Int = 0,
    slotType: InventoryType.SlotType = InventoryType.SlotType.CONTAINER,
    click: ClickType = ClickType.LEFT,
    hotbarKey: Int = -1,
) : InventoryClickEvent(view, slotType, slot, click, action, hotbarKey) {

    private var currentItem: ItemStack? = AIR

    override fun getCurrentItem(): ItemStack? = currentItem
    override fun setCurrentItem(stack: ItemStack?) {
        currentItem = stack
    }
}
