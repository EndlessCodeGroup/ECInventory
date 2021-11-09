package ru.endlesscode.inventory.configuration.data

import kotlinx.serialization.Serializable
import ru.endlesscode.inventory.slot.Slot

@Serializable
internal data class SlotConfig(
    val name: String,
    val texture: String? = null,
    val type: Slot.Type = Slot.Type.STORAGE,
    val allowedItems: List<String> = emptyList(),
    val maxStackSize: Int = 1,
)
