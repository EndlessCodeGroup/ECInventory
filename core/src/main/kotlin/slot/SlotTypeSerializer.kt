package ru.endlesscode.rpginventory.slot

import ru.endlesscode.rpginventory.util.ConfigEnumSerializer

internal object SlotTypeSerializer : ConfigEnumSerializer<Slot.Type>(
    serialName = Slot.Type::class.java.canonicalName,
    values = enumValues(),
)
