package ru.endlesscode.rpginventory.configuration.data

import kotlinx.serialization.Serializable
import ru.endlesscode.rpginventory.slot.Slot

@Serializable
internal data class SlotConfig(
    val name: String,
    val texture: String,
    val type: Slot.Type = Slot.Type.STORAGE,
    val allowedItems: List<String> = emptyList(),
    val maxStackSize: Int = 1,
)
