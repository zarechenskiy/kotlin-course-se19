package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testGreeting() {
        assertEquals("Hello, world!", getGreeting())
    }
}