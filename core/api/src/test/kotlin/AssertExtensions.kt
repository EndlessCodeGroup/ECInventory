package ru.endlesscode.rpginventory

import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

inline fun <reified T> assertInstanceOf(value: Any?, message: String? = null) {
    assertInstanceOf(T::class.java, value, message)
}

fun assertInstanceOf(aClass: Class<*>, value: Any?, message: String? = null) {
    assertThat(message, value, instanceOf(aClass))
}

inline fun <reified T : Throwable> assertFailsWith(
    message: String? = null,
    cause: KClass<out Throwable>? = null,
    noinline block: () -> Unit
) {
    try {
        block.invoke()
    } catch (e: Throwable) {
        if (e !is T) throw e
        message?.let { assertEquals(message, e.message, "Wrong exception message.") }
        cause?.let { assertInstanceOf(cause.java, e.cause, "Wrong exception cause.") }
        return
    }

    fail("Exception ${T::class.java} expected.")
}
