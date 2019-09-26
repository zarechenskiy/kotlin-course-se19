package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.PrintStream

class IntegrationTests {

    private val testDirectory = "src/test/data"

    private fun doTest(testName: String) {
        val stream = ByteArrayOutputStream()
        System.setOut(PrintStream(stream))

        main(arrayOf("$testDirectory/$testName.fun"))

        assertEquals(
            String(FileInputStream("$testDirectory/$testName.out").readBytes()),
            stream.toString()
        )
    }

    @Test
    fun testIf() {
        doTest("if")
    }

    @Test
    fun testRecursion() {
        doTest("recursion")
    }

    @Test
    fun testNestedFunctions() {
        doTest("nestedFunctions")
    }

    @Test
    fun testExpressions() {
        doTest("expressions")
    }
}