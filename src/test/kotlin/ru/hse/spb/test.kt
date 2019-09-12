package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun simpleTest() {
        val edgeList = arrayOf(
                listOf(2),
                listOf(2),
                listOf(0, 1, 3),
                listOf(2, 4, 5),
                listOf(3),
                listOf(3)
        )
        val universities = listOf(0, 4, 5, 1)
        assertEquals(6, maximalRoadsLength(edgeList, universities))
    }

    @Test
    fun bigTest() {
        val n = 200000
        val edgeList = Array(n) { v ->
            when (v) {
                0 -> listOf(1)
                n - 1 -> listOf(n - 2)
                else -> listOf(v - 1, v + 1)
            }
        }
        val universities = List(n) { it }

        val m = (n / 2).toLong()
        val answer = m * m

        assertEquals(answer, maximalRoadsLength(edgeList, universities))
    }

}