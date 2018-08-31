package ru.endlesscode.rpginventory.inventory

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Class copied from CraftBukkit
 */
class InventoryIterator internal constructor(
        private val inventory: Inventory,
        private var nextIndex: Int = 0
) : MutableListIterator<ItemStack> {

    private var lastDirection: Boolean? = null // true = forward, false = backward, null = haven't moved yet

    override fun hasNext(): Boolean {
        return nextIndex < inventory.size
    }

    override fun next(): ItemStack {
        lastDirection = true
        return inventory.getItem(nextIndex++)
    }

    override fun nextIndex(): Int {
        return nextIndex
    }

    override fun hasPrevious(): Boolean {
        return nextIndex > 0
    }

    override fun previous(): ItemStack {
        lastDirection = false
        return inventory.getItem(--nextIndex)
    }

    override fun previousIndex(): Int {
        return nextIndex - 1
    }

    override fun set(element: ItemStack) {
        lastDirection?.let {
            val i = if (it) nextIndex - 1 else nextIndex
            inventory.setItem(i, element)
        } ?: run {
            error("No current item!")
        }
    }

    override fun add(element: ItemStack) {
        sizeChangeUnsupported()
    }

    override fun remove() {
        sizeChangeUnsupported()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun sizeChangeUnsupported(): Nothing {
        throw UnsupportedOperationException("Can't change the size of an inventory!")
    }
}
