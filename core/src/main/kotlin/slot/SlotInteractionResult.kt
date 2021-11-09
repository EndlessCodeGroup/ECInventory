package ru.endlesscode.inventory.slot

import org.bukkit.inventory.ItemStack

/** Result of [SlotInteraction]. */
internal sealed interface SlotInteractionResult {

    object Accept : SlotInteractionResult
    object Deny : SlotInteractionResult

    data class Change(
        val currentItemReplacement: ItemStack? = null,
        val cursorReplacement: ItemStack? = null,
    ) : SlotInteractionResult
}
