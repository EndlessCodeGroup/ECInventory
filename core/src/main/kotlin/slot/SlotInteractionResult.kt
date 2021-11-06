package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack

sealed interface SlotInteractionResult {

    object Cancel : SlotInteractionResult

    data class Success(
        val cursorItem: ItemStack,
        val syncCursor: Boolean = false,
        val syncSlot: Boolean = false,
    ) : SlotInteractionResult
}

