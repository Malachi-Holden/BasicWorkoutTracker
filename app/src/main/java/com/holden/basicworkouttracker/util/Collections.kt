package com.holden.basicworkouttracker.util

fun <E> List<E>.replaced(index: Int, newVal: E) =
    slice(0 until index) + newVal + slice(index + 1 until size)

fun <E> List<E>.removed(index: Int) = slice(0 until index) + slice(index + 1 until size)


fun <T>List<T>.inserted(item: T, index: Int) = slice(0 until index) + item + slice(index until size)

fun <T, R: Any>List<T>.mapNotNullorNull(transform: (T) -> R?) = mapNotNull(transform).run {
    if (size < this@mapNotNullorNull.size) null else this
}