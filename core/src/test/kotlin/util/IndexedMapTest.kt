/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.util

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

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
        assertSoftly {
            map.getByIndex(0) shouldBe "Three"
            map.getByIndex(1) shouldBe "Nine"
            map.getByIndex(2) shouldBe "Ten"
        }
    }

    @Test
    fun `get keys by index`() {
        assertSoftly {
            map.getKeyByIndex(0) shouldBe 3
            map.getKeyByIndex(1) shouldBe 9
            map.getKeyByIndex(2) shouldBe 10
        }
    }

    @Test
    fun `get keys index`() {
        assertSoftly {
            map.getIndexOf(3) shouldBe 0
            map.getIndexOf(9) shouldBe 1
            map.getIndexOf(10) shouldBe 2
            map.getIndexOf(0) shouldBe -1
        }
    }

    @Test
    fun `put values with replacement and then get it by index`() {
        map[9] = "Nine[x2]"

        assertSoftly {
            map.getByIndex(0) shouldBe "Three"
            map.getByIndex(1) shouldBe "Nine[x2]"
            map.getByIndex(2) shouldBe "Ten"
        }
    }

    @Test
    fun `put many values to non-empty map`() {
        map.putAll(
            mapOf(
                6 to "Six",
                3 to "Three[x2]",
                100 to "One hundred"
            )
        )

        assertSoftly {
            map.getByIndex(0) shouldBe "Three[x2]"
            map.getByIndex(1) shouldBe "Six"
            map.getByIndex(2) shouldBe "Nine"
            map.getByIndex(3) shouldBe "Ten"
            map.getByIndex(4) shouldBe "One hundred"
        }
    }

    @Test
    fun `remove items`() {
        map.remove(9)

        map.getByIndex(1) shouldBe "Ten"
    }

    @Test
    fun `create indexed map from existing map`() {
        val existingMap = mapOf(
            3 to "Three",
            4 to "Four",
            0 to "Zero"
        )
        val indexedMap = existingMap.asIndexedMap()

        assertSoftly {
            indexedMap.getByIndex(0) shouldBe "Zero"
            indexedMap.getByIndex(1) shouldBe "Three"
            indexedMap.getByIndex(2) shouldBe "Four"
        }
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

        existingMap.size shouldBe 3
        indexedMap.size shouldBe 0
    }

    @Test
    fun `get value by non-existing index`() {
        shouldThrow<IndexOutOfBoundsException> {
            map.getByIndex(-1)
        }
    }

    @Test
    fun `get key by non-existing index`() {
        shouldThrow<IndexOutOfBoundsException> {
            map.getKeyByIndex(-1)
        }
    }

    @Test
    fun `get first element after clear`() {
        shouldThrow<IndexOutOfBoundsException> {
            map.clear()
            map.getByIndex(0)
        }
    }
}
