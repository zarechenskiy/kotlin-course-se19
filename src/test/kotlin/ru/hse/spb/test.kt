package ru.hse.spb

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class TestSource {
    private val testDirectory = "src/test/samples"

    private fun doTest(testName: String) {
        val outputStream = ByteArrayOutputStream()
        solve(FileInputStream("$testDirectory/$testName.in"), outputStream)
        assertEquals(String(FileInputStream("$testDirectory/$testName.out").readAllBytes()), outputStream.toString())
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