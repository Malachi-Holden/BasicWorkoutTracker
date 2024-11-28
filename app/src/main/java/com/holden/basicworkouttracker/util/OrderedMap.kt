package com.holden.basicworkouttracker.util

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

/**
 * Interface that acts like both a map and a list. Remembers the order of the items placed in it,
 * allows for both lookup by index and by key
 *
 * Does not allow duplicate keys
 */
interface OrderedMap<K, V>: Map<K, V> {

    fun getKeyAtIndex(index: Int): K

    fun getAtIndex(index: Int): Pair<K, V> {
        val key = getKeyAtIndex(index)
        val value = this[key] ?: throw IllegalArgumentException("Value does not exist for key $key at index $index")
        return key to value
    }

    fun indexForKey(key: K): Int

    fun remove(key: K): OrderedMap<K, V> = removeAtIndex(indexForKey(key))

    fun removeAtIndex(index: Int): OrderedMap<K, V>

    /**
     * Adds a new key-value pair to the collection at the specified index. If the key already
     * exists, it will be removed from the collection
     */
    fun insert(entry: Pair<K, V>, index: Int): OrderedMap<K, V>

    fun append(entry: Pair<K, V>) = insert(entry, size)

    fun replaceAtIndex(newEntry: Pair<K, V>, index: Int): OrderedMap<K, V> {
        val removed = removeAtIndex(index)
        val added = removed.insert(newEntry, index)
        return added
    }

    fun replace(newValue: V, key: K) = replaceAtIndex(key to newValue, indexForKey(key))

    fun toList(): List<Pair<K, V>> = List(size) {
        getAtIndex(it)
    }
}

fun <K, V> orderedMapOf(vararg pairs: Pair<K, V>): OrderedMap<K, V> = pairs
        .toMap()
        .toOrderedMap(pairs.toList())


fun <K, V> List<Pair<K, V>>.toOrderedMap() = toMap().toOrderedMap(this)

fun <K, V> OrderedMap<K, V>.toPairs(): List<Pair<K, V>> = buildList {
    for (i in 0 until this@toPairs.size) {
        val (key, value) = getAtIndex(i)
        if (value != null) {
            add(key to value)
        }
    }
}

fun <K, V>Map<K,V>.toOrderedMap(pairs: List<Pair<K, V>>) : OrderedMap<K, V> = object : Map<K, V> by this, OrderedMap<K, V> {
    override fun getKeyAtIndex(index: Int): K = pairs[index].first
    override fun indexForKey(key: K): Int = pairs.indexOfFirst { it.first == key }

    override fun insert(entry: Pair<K, V>, index: Int): OrderedMap<K, V>
        = (this + entry).toOrderedMap(
            pairs
                .filterNot { it.first == entry.first }
                .inserted(entry, index)
        )


    override fun removeAtIndex(index: Int): OrderedMap<K, V>
    = (this@toOrderedMap - pairs[index].first).toOrderedMap(pairs.removed(index))


    override fun equals(other: Any?): Boolean {
        if (other !is OrderedMap<*, *>) return false
        if (other.size != pairs.size) return false
        for ((i, pair) in pairs.withIndex()) {
            if (other.getAtIndex(i) != pair) return false
        }
        return true
    }

    override fun toString(): String = toList().joinToString()
}

fun <K, V>OrderedMap<K, V>.swap(first: Int, second: Int): OrderedMap<K, V> {
    if (second < first) return swap(second, first)
    val firstPair = getAtIndex(first)
    val secondPair = getAtIndex(second)
    val result =  removeAtIndex(second)
        .removeAtIndex(first)
        .insert(secondPair, first)
        .insert(firstPair, second)
    return result
}

fun <K, V> LazyListScope.items(orderedMap: OrderedMap<K, V>, itemContent: @Composable (K, V?) -> Unit) = items(orderedMap.size) {
    val (key, value) = orderedMap.getAtIndex(it)
    itemContent(key, value)
}
