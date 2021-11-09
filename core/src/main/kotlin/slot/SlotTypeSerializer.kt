package ru.endlesscode.inventory.slot

import ru.endlesscode.inventory.util.ConfigEnumSerializer

internal object SlotTypeSerializer : ConfigEnumSerializer<Slot.Type>(
    serialName = Slot.Type::class.java.canonicalName,
    values = enumValues(),
)
