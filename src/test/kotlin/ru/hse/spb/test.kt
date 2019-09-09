package ru.hse.spb

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testSolver1() {
        val input =
            "0 0 5 5\n" +
                    "3 2\n" +
                    "-1 -1\n" +
                    "-1 0\n"
        val inputStream = input.byteInputStream(Charsets.UTF_8)
        assertEquals(3.729935587093554, solver(inputStream), 1e-6)
    }

    @Test
    fun testSolver2() {
        val input =
            "0 0 0 1000\n" +
                    "100 1000\n" +
                    "-50 0\n" +
                    "50 0\n"
        val inputStream = input.byteInputStream(Charsets.UTF_8)
        assertEquals(11.547005383792516398, solver(inputStream), 1e-6)
    }

    @Test
    fun testSolver3() {
        val input =
            "-753 8916 -754 8915\n" +
                    "1000 33\n" +
                    "999 44\n" +
                    "-44 -999\n"
        val inputStream = input.byteInputStream(Charsets.UTF_8)
        assertEquals(33.000003244932003099, solver(inputStream), 1e-6)
    }
}
