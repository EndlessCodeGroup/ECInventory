package ru.endlesscode.inventory

import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.slot.Slot
import java.util.*

internal data class InventoryLayoutImpl(
    override val name: String,
    override val emptySlotTexture: ItemStack,
    override val slotsMap: SortedMap<Int, Slot>,
) : InventoryLayout {

    init {
        require(slotsMap.isNotEmpty()) { "Slots map shouldn't be empty." }
    }
}
