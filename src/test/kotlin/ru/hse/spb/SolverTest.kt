package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class SolverTest {
    @Test
    fun testFirstSample() {
        val solver = Solver(
                4,
                arrayOf(1, 2, 3, 4).toIntArray(),
                3,
                arrayOf(
                        arrayOf(2, 3).toIntArray(),
                        arrayOf(1, 2, 2).toIntArray(),
                        arrayOf(2, 1).toIntArray()
                ),
                IntArray(4)
        )
        solver.solve()
        assertArrayEquals(arrayOf(3, 2, 3, 4).toIntArray(), solver.ans)
    }

    @Test
    fun testSecondSample() {
        val solver = Solver(
                5,
                arrayOf(3, 50, 2, 1, 10).toIntArray(),
                3,
                arrayOf(
                        arrayOf(1, 2, 0).toIntArray(),
                        arrayOf(2, 8).toIntArray(),
                        arrayOf(1, 3, 20).toIntArray()
                ),
                IntArray(5)
        )
        solver.solve()
        assertArrayEquals(arrayOf(8, 8, 20, 8, 10).toIntArray(), solver.ans)
    }
}