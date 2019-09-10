package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class TestSource {

    private val testDirectory = "src/test/data"

    private fun doTest(testName: String) {
        val stream = ByteArrayOutputStream()
        executeSolution(FileInputStream("$testDirectory/$testName.in"), stream)
        assertEquals(
            String(FileInputStream("$testDirectory/$testName.out").readAllBytes()),
            stream.toString()
        )
    }

    @Test
    fun testSample1() {
        doTest("sample1")
    }

    @Test
    fun testSample2() {
        doTest("sample2")
    }
}