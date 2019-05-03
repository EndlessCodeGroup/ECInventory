package ru.endlesscode.rpginventory

import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat

inline fun <reified T> assertInstanceOf(value: Any?) {
    assertThat(value, instanceOf(T::class.java))
}
