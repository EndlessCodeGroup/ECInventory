package ru.endlesscode.rpginventory.extensions

import org.junit.Assert
import org.junit.Test

class MathExtensionsKtTest {

    @Test
    fun `round to power`() {
        Assert.assertEquals(0, 0.roundToPowerOf(2))
        Assert.assertEquals(10, 7.roundToPowerOf(5))
        Assert.assertEquals(7, 7.roundToPowerOf(7))
        Assert.assertEquals(-12, (-7).roundToPowerOf(6))
    }
}
