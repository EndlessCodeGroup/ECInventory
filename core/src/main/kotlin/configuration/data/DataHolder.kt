package ru.endlesscode.rpginventory.configuration.data

import kotlinx.serialization.hocon.Hocon
import ru.endlesscode.mimic.items.BukkitItemsRegistry
import ru.endlesscode.rpginventory.InventoryLayout
import ru.endlesscode.rpginventory.configuration.ConfigurationCollector
import ru.endlesscode.rpginventory.internal.DI
import ru.endlesscode.rpginventory.slot.ItemValidator
import ru.endlesscode.rpginventory.slot.Slot
import ru.endlesscode.rpginventory.slot.SlotImpl
import ru.endlesscode.rpginventory.util.Log
import ru.endlesscode.rpginventory.util.MAX_STACK_SIZE
import java.nio.file.Path

internal class DataHolder(
    private val itemsRegistry: BukkitItemsRegistry,
    private val collector: ConfigurationCollector,
) {

    constructor(
        itemsRegistry: BukkitItemsRegistry,
        pluginDataDir: Path,
        hocon: Hocon = DI.hocon,
    ) : this(itemsRegistry, ConfigurationCollector(pluginDataDir.resolve("data"), hocon))

    var slots: Map<String, Slot> = emptyMap()
        private set
    var inventories: Map<String, InventoryLayout> = emptyMap()
        private set

    init {
        collectData()
    }

    fun reload() {
        collectData()
    }

    private fun collectData() {
        val data = collector.collect<DataConfig>()
        slots = data.slots.mapValues { (id, config) -> createSlot(id, config) }
    }

    private fun createSlot(id: String, config: SlotConfig): Slot {
        val prefix = "Parsing slot '$id':"
        val textureItem = requireNotNull(itemsRegistry.getItem(config.texture)) {
            "$prefix Unknown texture '${config.texture}'. $errorMimicIdExplanation"
        }
        val correctMaxStackSize = config.maxStackSize.coerceIn(1, MAX_STACK_SIZE)
        if (correctMaxStackSize != config.maxStackSize) {
            Log.w(
                "$prefix max stack size should be in range 1..$MAX_STACK_SIZE, but was '${config.maxStackSize}'.",
                "Will be used $correctMaxStackSize instead, please fix slot config.",
            )
        }

        return SlotImpl(
            id = id,
            name = config.name,
            texture = textureItem,
            type = config.type,
            contentValidator = ItemValidator.any,
            maxStackSize = correctMaxStackSize,
        )
    }
}
