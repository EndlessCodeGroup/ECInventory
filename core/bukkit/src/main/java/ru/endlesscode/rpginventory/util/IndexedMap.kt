package ru.endlesscode.rpginventory.util

import java.util.*

/**
 * Map where you can get elements by theirs index.
 */
internal class IndexedMap<K : Comparable<K>, V> private constructor(
        private val content: SortedMap<K, V>
) : SortedMap<K, V> by content {

    private var indexedKeys: MutableList<K>

    init {
        this.indexedKeys = content.keys.toMutableList()
    }

    /**
     * Creates empty [IndexedMap]
     */
    constructor() : this(sortedMapOf())

    /**
     * Creates [IndexedMap] filled with elements from [map]
     */
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
            @Suppress("NestedLambdaShadowedImplicitParameter")
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

    /**
     * Returns index of the element with given [key], or -1 if the map does not contain such element.
     */
    fun getIndexOf(key: K): Int {
        return indexedKeys.indexOfFirst { key == it }
    }

    /**
     * Returns element's key by [index].
     *
     * @throws IndexOutOfBoundsException when the map doesn't contain a key for the specified index.
     */
    fun getKeyByIndex(index: Int): K {
        return indexedKeys[index]
    }
}

/**
 * Converts this [Map] to a [IndexedMap] so indexes order will be in key order.
 */
internal fun <K : Comparable<K>, V> Map<out K, V>.asIndexedMap(): IndexedMap<K, V> {
    return IndexedMap(this)
}
