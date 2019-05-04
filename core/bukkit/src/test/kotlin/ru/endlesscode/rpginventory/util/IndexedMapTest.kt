package ru.endlesscode.rpginventory.util

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexedMapTest {

    private val map = IndexedMap<Int, String>()

    @BeforeTest
    fun setUp() {
        map[9] = "Nine"
        map[3] = "Three"
        map[10] = "Ten"
    }

    @Test
    fun `get values by index`() {
        assertEquals("Three", map.getByIndex(0))
        assertEquals("Nine", map.getByIndex(1))
        assertEquals("Ten", map.getByIndex(2))
    }

    @Test
    fun `get keys by index`() {
        assertEquals(3, map.getKeyByIndex(0))
        assertEquals(9, map.getKeyByIndex(1))
        assertEquals(10, map.getKeyByIndex(2))
    }

    @Test
    fun `get keys index`() {
        assertEquals(0, map.getIndexOf(3))
        assertEquals(1, map.getIndexOf(9))
        assertEquals(2, map.getIndexOf(10))
        assertEquals(-1, map.getIndexOf(0))
    }

    @Test
    fun `put values with replacement and then get it by index`() {
        map[9] = "Nine[x2]"

        assertEquals("Three", map.getByIndex(0))
        assertEquals("Nine[x2]", map.getByIndex(1))
        assertEquals("Ten", map.getByIndex(2))
    }

    @Test
    fun `put many values to non-empty map`() {
        map.putAll(mapOf(
                6 to "Six",
                3 to "Three[x2]",
                100 to "One hundred"
        ))

        assertEquals("Three[x2]", map.getByIndex(0))
        assertEquals("Six", map.getByIndex(1))
        assertEquals("Nine", map.getByIndex(2))
        assertEquals("Ten", map.getByIndex(3))
        assertEquals("One hundred", map.getByIndex(4))
    }

    @Test
    fun `remove items`() {
        map.remove(9)

        assertEquals("Ten", map.getByIndex(1))
    }

    @Test
    fun `create indexed map from existing map`() {
        val existingMap = mapOf(
                3 to "Three",
                4 to "Four",
                0 to "Zero"
        )
        val indexedMap = existingMap.asIndexedMap()

        assertEquals("Zero", indexedMap.getByIndex(0))
        assertEquals("Three", indexedMap.getByIndex(1))
        assertEquals("Four", indexedMap.getByIndex(2))
    }

    @Test
    fun `changing of indexed map shouldn't affect source map`() {
        val existingMap = mapOf(
                3 to "Three",
                4 to "Four",
                0 to "Zero"
        )
        val indexedMap = existingMap.asIndexedMap()
        indexedMap.clear()

        assertEquals(3, existingMap.size)
        assertEquals(0, indexedMap.size)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `get value by non-existing index`() {
        map.getByIndex(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `get key by non-existing index`() {
        map.getKeyByIndex(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `get first element after clear`() {
        map.clear()
        map.getByIndex(0)
    }
}
