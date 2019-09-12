package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testGoodSubstr1() {
        assertEquals(4, getGoodSubstrNumber("0110"))
    }

    @Test
    fun testGoodSubstr2() {
        assertEquals(3, getGoodSubstrNumber("0101"))
    }

    @Test
    fun testGoodSubstr3() {
        assertEquals(4, getGoodSubstrNumber("00001000"))
    }

    @Test
    fun testGoodSubstr4() {
        assertEquals(3, getGoodSubstrNumber("0001000"))
    }

    @Test
    fun testZeroString() {
        assertEquals(0, getGoodSubstrNumber("0000000"))
    }
}