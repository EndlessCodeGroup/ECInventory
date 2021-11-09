package ru.endlesscode.inventory.test

import org.bukkit.event.inventory.*
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.util.AIR

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

class TestInventoryDragEvent(
    view: InventoryView = TestInventoryView(),
    oldCursor: ItemStack,
    newCursor: ItemStack? = null,
    rightClick: Boolean = false,
    slots: Map<Int, ItemStack> = mapOf(0 to oldCursor),
) : InventoryDragEvent(view, newCursor, oldCursor, rightClick, slots)
