package ru.endlesscode.rpginventory.configuration.data

import kotlinx.serialization.hocon.Hocon
import ru.endlesscode.mimic.items.BukkitItemsRegistry
import ru.endlesscode.rpginventory.configuration.ConfigurationCollector
import ru.endlesscode.rpginventory.internal.DI
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

    var data: DataConfig
        private set

    init {
        data = collectData()
    }

    fun reload() {
        data = collectData()
    }

    private fun collectData(): DataConfig {
        val data = collector.collect<DataConfig>()
        data.slots.forEach { (id, slot) -> slot.validate(id) }
        data.inventories.forEach { (id, inventory) -> inventory.validate(id) }
        return data
    }

    private fun SlotConfig.validate(id: String) {
        val prefix = "Can't parse slot with ID '$id'."
        check(maxStackSize in 1..MAX_STACK_SIZE) {
            "$prefix Max stack size should be in range 1..$MAX_STACK_SIZE, bit it was $maxStackSize."
        }
        check(itemsRegistry.isItemExists(texture)) {
            "$prefix Unknown texture '$texture'. $errorMimicIdExplanation"
        }
    }

    private fun InventoryConfig.validate(id: String) {
        val prefix = "Can't parse inventory with ID '$id'."
        check(emptySlotTexture == null || itemsRegistry.isItemExists(emptySlotTexture)) {
            "$prefix Unknown empty slot texture '$emptySlotTexture'. $errorMimicIdExplanation"
        }
        check(slots.isNotEmpty()) { "$prefix. Inventory should contain at least one slot." }
        check(slots.values.all { it in data.slots.keys })
    }
}
