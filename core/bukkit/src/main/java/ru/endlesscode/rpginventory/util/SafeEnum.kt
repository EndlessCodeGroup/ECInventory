package ru.endlesscode.rpginventory.util

internal inline fun <reified T : Enum<T>> safeValueOf(value: String): T? {
    return try {
        enumValueOf<T>(value)
    } catch (e: IllegalArgumentException) {
        null
    }
}
