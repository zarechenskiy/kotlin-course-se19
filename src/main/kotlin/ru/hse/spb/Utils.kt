package ru.hse.spb

// Автоформаттер сказал так, хотя выглядит это как что-то неправильное.
fun Boolean.toInt() = if (this) {
    1
} else {
    0
}

fun Int.toBoolean() = this != 0
