package com.holden.basicworkouttracker.util

fun String.toDoubleWithWhiteSpaceOrNull() = if (isBlank()) 0.0 else toDoubleOrNull()