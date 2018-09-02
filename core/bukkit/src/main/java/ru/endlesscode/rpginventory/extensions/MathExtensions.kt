package ru.endlesscode.rpginventory.extensions

/**
 * Rounds the number up to given [power].
 */
fun Int.roundToPowerOf(power: Int): Int {
    return this / power * power
}
