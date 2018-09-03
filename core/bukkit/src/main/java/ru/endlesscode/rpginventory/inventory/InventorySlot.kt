package ru.endlesscode.rpginventory.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.extensions.isEmpty
import ru.endlesscode.rpginventory.extensions.isNotEmpty

/**
 * Represents slot in the inventory, that has own behavior.
 *
 * @param prototype The prototype that used to determine slot's behavior.
 *
 * @property holder The inventory that contain this slot.
 * @property position Position of the slot in vanilla inventory.
 * @property content The item stored in the slot. Or air if the slot is empty.
 */
class InventorySlot(
        prototype: Slot,
        val holder: RPGInventory,
        val position: Int
) : Slot by prototype {

    var content: ItemStack = ItemStack(Material.AIR)
        set(value) {
            field = content
            if (value.isNotEmpty() && this.maxStackSize > 0 && value.amount > this.maxStackSize) {
                value.amount = this.maxStackSize
            }

            // We need to sync this change with the inventory's view it it is open
            holder.syncSlotWithView(this)
        }

    override var maxStackSize: Int = 1
        set(value) {
            field = value
            updateHolderMaxStackSize()
        }

    init {
        if (prototype is InventorySlot) {
            error("InventorySlot can't be used as prototype")
        }

        updateHolderMaxStackSize()
    }

    /**
     * Returns true if slot's content is empty.
     */
    fun isEmpty(): Boolean{
        return content.isEmpty()
    }

    /**
     * Returns [content] if it isn't empty and [slotHolder] otherwise.
     */
    fun getContentOrHolder(): ItemStack {
        return if (isEmpty()) slotHolder else content
    }

    private fun updateHolderMaxStackSize() {
        if (holder.maxStackSize < maxStackSize) {
            holder.maxStackSize = maxStackSize
        }
    }
}
