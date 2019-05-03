package ru.endlesscode.rpginventory.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class MathExtensionsKtTest {

    @Test
    fun `round to power`() {
        assertEquals(0, 0.roundToPowerOf(2))
        assertEquals(10, 7.roundToPowerOf(5))
        assertEquals(7, 7.roundToPowerOf(7))
        assertEquals(-12, (-7).roundToPowerOf(6))
    }
}
