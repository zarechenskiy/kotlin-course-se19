package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class TestSource {
    @Test
    fun firstTest() {
        val result = solve(5, 2, listOf(4, 2, 1, 10, 2))
        assertEquals(20, result.totalCost.intValueExact())
        assertEquals(listOf(3, 6, 7, 4, 5), result.planesIndexes)
    }
}