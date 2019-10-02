package ru.hse.spb.tex

import org.junit.jupiter.api.Assertions.assertEquals

fun <Es : Elements> test(expected: String, es: Es, init: Es.() -> Unit) {
    assertEquals(expected.replace("    ", "\t"), es.also(init).stringRepresentation)
}