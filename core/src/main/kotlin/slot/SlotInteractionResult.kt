package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack

sealed interface SlotInteractionResult {

    object Deny : SlotInteractionResult

    object Accept : SlotInteractionResult

    data class Change(
        val currentItemReplacement: ItemStack? = null,
        val cursorReplacement: ItemStack? = null,
        val syncSlot: Boolean = false,
    ) : SlotInteractionResult
}
