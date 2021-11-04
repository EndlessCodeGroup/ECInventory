package ru.endlesscode.rpginventory.configuration.data

import kotlinx.serialization.Serializable

@Serializable
internal data class DataConfig(
    val inventories: Map<String, InventoryConfig>,
    val slots: Map<String, SlotConfig>,
)
