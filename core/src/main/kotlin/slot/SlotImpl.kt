package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.util.MAX_STACK_SIZE

internal data class SlotImpl(
    override val id: String,
    override val name: String,
    override val texture: ItemStack,
    override val type: Slot.Type,
    override val contentValidator: ItemValidator,
    override val maxStackSize: Int,
) : Slot {

    init {
        require(maxStackSize in 1..MAX_STACK_SIZE) {
            "Can't create slot '$id'. Max stack size should be in range 1..$MAX_STACK_SIZE, bit it was $maxStackSize."
        }
    }
}
