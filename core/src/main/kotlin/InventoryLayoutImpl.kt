package ru.endlesscode.rpginventory

import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.slot.Slot
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
