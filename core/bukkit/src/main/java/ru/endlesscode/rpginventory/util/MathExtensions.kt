package ru.endlesscode.rpginventory.util


fun Int.roundToPowerOf(power: Int): Int {
    return this / power * power
}
