package ru.endlesscode.rpginventory.slot

import org.bukkit.inventory.ItemStack

/** Result of [SlotInteraction]. */
internal sealed interface SlotInteractionResult {

    object Accept : SlotInteractionResult
    object Deny : SlotInteractionResult

    data class Change(
        val currentItemReplacement: ItemStack? = null,
        val cursorReplacement: ItemStack? = null,
        val syncSlot: Boolean = false,
    ) : SlotInteractionResult
}