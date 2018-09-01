package ru.endlesscode.rpginventory

/**
 * Map where you can get elements by theirs index.
 *
 * @param content The map that should be indexed. Better to use here [java.util.SortedMap]
 * or [java.util.LinkedHashMap].
 */
class IndexedMap<K, V>(content: Map<K, V>) : Map<K, V> by content {

    private var indexedKeys: List<K> = content.keys.toList()

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

fun <K, V> Map<K, V>.toIndexedMap(): IndexedMap<K, V> {
    return IndexedMap(this)
}
