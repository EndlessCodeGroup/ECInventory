package ru.endlesscode.inventory.configuration.data

import kotlinx.serialization.Serializable

@Serializable
internal data class DataConfig(
    val inventories: Map<String, InventoryConfig> = emptyMap(),
    val slots: Map<String, SlotConfig> = emptyMap(),
)
