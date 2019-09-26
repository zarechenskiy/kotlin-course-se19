package ru.hse.spb

// Auto-formatter says it's ok, but it looks ugly for me.
fun Boolean.toInt() = if (this) {
    1
} else {
    0
}

fun Int.toBoolean() = this != 0
