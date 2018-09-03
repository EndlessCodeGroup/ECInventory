package ru.endlesscode.rpginventory.extensions

import kotlin.math.sign

/**
 * Rounds the number up to given [power].
 */
fun Int.roundToPowerOf(power: Int): Int {
    val additional = if (this % power == 0) 0 else this.sign
    return (this / power + additional) * power
}
