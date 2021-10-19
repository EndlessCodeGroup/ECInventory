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

import java.util.*

/** Map where you can get elements by theirs index. */
internal class IndexedMap<K : Comparable<K>, V> private constructor(
    private val content: SortedMap<K, V>
) : SortedMap<K, V> by content {

    private var indexedKeys: MutableList<K> = content.keys.toMutableList()

    /** Creates empty [IndexedMap]. */
    constructor() : this(sortedMapOf())

    /** Creates [IndexedMap] filled with elements from [map]. */
    constructor(map: Map<out K, V>) : this(map.toSortedMap())

    override fun clear() {
        content.clear()
        indexedKeys.clear()
    }

    override fun put(key: K, value: V): V? {
        val isReplacement = key in content
        val result = content.put(key, value)
        val index = content.keys.indexOfFirst { it == key }
        if (isReplacement) {
            indexedKeys[index] = key
        } else {
            indexedKeys.add(index, key)
        }

        return result
    }

    override fun putAll(from: Map<out K, V>) {
        content.putAll(from)
        indexedKeys = content.keys.toMutableList()
    }

    override fun remove(key: K): V? {
        indexedKeys.remove(key)
        return content.remove(key)
    }

    /**
     * Returns element by [index], or throws an exception if there no such element.
     *
     * @throws IndexOutOfBoundsException when the map doesn't contain a value for the specified index.
     */
    fun getByIndex(index: Int): V {
        val key = indexedKeys[index]
        return this.getValue(key)
    }

    /** Returns index of the element with given [key], or -1 if the map does not contain such element. */
    fun getIndexOf(key: K): Int = indexedKeys.indexOfFirst { key == it }

    /**
     * Returns element's key by [index].
     *
     * @throws IndexOutOfBoundsException when the map doesn't contain a key for the specified index.
     */
    fun getKeyByIndex(index: Int): K = indexedKeys[index]
}

/** Converts this [Map] to a [IndexedMap] so indexes order will be in key order. */
internal fun <K : Comparable<K>, V> Map<out K, V>.asIndexedMap(): IndexedMap<K, V> = IndexedMap(this)
