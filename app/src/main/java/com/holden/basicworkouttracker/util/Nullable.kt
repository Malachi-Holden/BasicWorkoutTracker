package com.holden.basicworkouttracker.util

interface NullableScope {
    fun <A> A?.bind(): A

    class WasNull: Exception()
}

fun <A> bindNullable(action: NullableScope.() -> A?): A? {
    val scope = object : NullableScope {
        override fun <A> A?.bind(): A {
            return this ?: throw NullableScope.WasNull()
        }
    }

    return try {
        scope.action()
    } catch (e: NullableScope.WasNull) {
        null
    }
}