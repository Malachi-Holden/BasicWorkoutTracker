package com.holden.basicworkouttracker.util

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.holden.basicworkouttracker.util.inserted
import com.holden.basicworkouttracker.util.removed


interface OrderedMap<K, V>: Map<K, V> {

    fun getKeyAtIndex(index: Int): K

    fun getAtIndex(index: Int): Pair<K, V?> {
        val key = getKeyAtIndex(index)
        val value = this[key]
        return key to value
    }

    fun indexForKey(key: K): Int

    fun remove(key: K): OrderedMap<K, V> = removeAtIndex(indexForKey(key))

    fun removeAtIndex(index: Int): OrderedMap<K, V>

    fun insert(entry: Pair<K, V>, index: Int): OrderedMap<K, V>

    fun append(entry: Pair<K, V>) = insert(entry, size)

    fun replaceAtIndex(newEntry: Pair<K, V>, index: Int): OrderedMap<K, V>
            = removeAtIndex(index)
        .insert(newEntry, index)

    fun replace(newValue: V, key: K) = replaceAtIndex(key to newValue, indexForKey(key))
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

        override fun insert(entry: Pair<K, V>, index: Int): OrderedMap<K, V> = (this + entry).toOrderedMap(pairs.inserted(entry, index))

        override fun removeAtIndex(index: Int): OrderedMap<K, V> = (this@toOrderedMap - pairs[index].first)
            .toOrderedMap(pairs.removed(index))

        override fun equals(other: Any?): Boolean {
            if (other !is OrderedMap<*, *>) return false
            if (other.size != pairs.size) return false
            for ((i, pair) in pairs.withIndex()) {
                if (other.getAtIndex(i) != pair) return false
            }
            return true
        }

        override fun toString(): String = pairs.toString()

    }

fun <K, V>Pair<K, V>.toMapEntry() = object : Map.Entry<K, V> {
    override val key = first
    override val value = second
}

fun <K, V> LazyListScope.items(orderedMap: OrderedMap<K, V>, itemContent: @Composable (Pair<K, V?>) -> Unit) = items(orderedMap.size) {
    itemContent(orderedMap.getAtIndex(it))
}
