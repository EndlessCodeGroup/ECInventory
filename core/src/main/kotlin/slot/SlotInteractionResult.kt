package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack

sealed interface SlotInteractionResult {

    object Deny : SlotInteractionResult

    object Accept : SlotInteractionResult

    data class Change(
        val cursorItem: ItemStack,
        val syncCursor: Boolean = false,
        val syncSlot: Boolean = false,
    ) : SlotInteractionResult
}

