package ru.endlesscode.inventory.configuration.data

import kotlinx.serialization.Serializable

@Serializable
internal data class InventoryConfig(
    val name: String,
    val emptySlotTexture: String? = null,
    val slots: Map<String, String>,
)
