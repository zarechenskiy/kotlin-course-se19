package ru.hse.spb

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class TestSource {
    private fun testRunner(input: String, output: String) {
        val inputStream = ByteArrayInputStream(input.toByteArray(Charsets.UTF_8))
        val outputStream = ByteArrayOutputStream()
        task(inputStream, outputStream)
        assertEquals(output, String(outputStream.toByteArray(), Charsets.UTF_8))
    }

    @Test
    fun testCase0() {
        testRunner(
            "8\n" +
                    "1 2\n" +
                    "2 3\n" +
                    "3 1\n" +
                    "3 4\n" +
                    "4 5\n" +
                    "5 6\n" +
                    "6 7\n" +
                    "6 8", "0 0 0 1 2 3 4 4\n"
        )
    }

    @Test
    fun testCase1() {
        testRunner(
            "5\n" +
                    "2 5\n" +
                    "4 5\n" +
                    "4 3\n" +
                    "4 2\n" +
                    "1 4", "1 0 1 0 0\n"
        )
    }

    @Test
    fun testCase2() {
        testRunner(
            "4\n" +
                    "4 3\n" +
                    "1 3\n" +
                    "2 1\n" +
                    "2 3", "0 0 0 1\n"
        )
    }
}