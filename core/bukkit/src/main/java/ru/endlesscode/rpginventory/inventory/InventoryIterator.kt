package ru.endlesscode.rpginventory.inventory

import org.bukkit.inventory.ItemStack

/**
 * Class copied from CraftBukkit
 */
class InventoryIterator internal constructor(
        private val inventory: RPGInventory,
        private var nextIndex: Int = 0
) : MutableListIterator<ItemStack> {

    private var lastDirection = Direction.NOT_MOVED

    override fun hasNext(): Boolean {
        return nextIndex < inventory.size
    }

    override fun next(): ItemStack {
        lastDirection = Direction.FORWARD
        return inventory.getItem(nextIndex++)
    }

    override fun nextIndex(): Int {
        return nextIndex
    }

    override fun hasPrevious(): Boolean {
        return nextIndex > 0
    }

    override fun previous(): ItemStack {
        lastDirection = Direction.BACKWARD
        return inventory.getItem(--nextIndex)
    }

    override fun previousIndex(): Int {
        return nextIndex - 1
    }

    override fun set(element: ItemStack) {
        if (lastDirection == Direction.NOT_MOVED) error("No current item!")

        val i = if (lastDirection == Direction.FORWARD) nextIndex - 1 else nextIndex
        inventory.setItem(i, element)
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

    private enum class Direction {
        FORWARD,
        BACKWARD,
        NOT_MOVED
    }
}
