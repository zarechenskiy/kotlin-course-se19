package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringWriter

class TestSource {
    @Test
    fun testPrintln() {
        val onlyPrintln = """
            |println(42)
        """.trimMargin()
        assertEquals("42\n", runTest(onlyPrintln))

        val multiParameterPrintln = """
            |println(42, 239, 23)
        """.trimMargin()
        assertEquals("42 239 23\n", runTest(multiParameterPrintln))

        val emptyPrintln = """
            |println()
        """.trimMargin()
        assertEquals("\n", runTest(emptyPrintln))

        val somePrints = """
            |println(42, 239, 23)
            |println(11)
            |println(51, 33)
        """.trimMargin()
        assertEquals("42 239 23\n11\n51 33\n", runTest(somePrints))
    }

    @Test
    fun testOperationsOrder() {
        val someMinusesInRow = """
            |println(1 - 1 - 1)
        """.trimMargin()
        assertEquals("-1\n", runTest(someMinusesInRow))

        val addAndMult = """
            |println(2 + 2 * 2)
        """.trimMargin()
        assertEquals("6\n", runTest(addAndMult))

        val braces = """
            |println((2 + 2) * 2)
        """.trimMargin()
        assertEquals("8\n", runTest(braces))
    }

    @Test
    fun testIf() {
        val trueIf = """
            |if (239 > 179) {
            |   println(42)
            |} else {
            |   println(-42)
            |}
        """.trimMargin()
        assertEquals("42\n", runTest(trueIf))

        val falseIf = """
            |if (239 < 179) {
            |   println(42)
            |} else {
            |   println(-42)
            |}
        """.trimMargin()
        assertEquals("-42\n", runTest(falseIf))

        val trueIfWithoutElse = """
            |if (239 > 179) {
            |   println(42)
            |}
        """.trimMargin()
        assertEquals("42\n", runTest(trueIfWithoutElse))

        val falseIfWithoutElse = """
            |if (239 < 179) {
            |   println(42)
            |}
        """.trimMargin()
        assertEquals("", runTest(falseIfWithoutElse))

        val continueExecutionAfterIf = """
            |if (239 > 179) {
            |   println(42)
            |} else {
            |   println(-42)
            |}
            |
            |println(5)
        """.trimMargin()
        assertEquals("42\n5\n", runTest(continueExecutionAfterIf))

        val returnInsideIf = """
            |if (239 > 179) {
            |   println(42)
            |   return 5
            |} else {
            |   println(-42)
            |}
            |
            |println(5)
        """.trimMargin()
        assertEquals("42\n", runTest(returnInsideIf))
    }

    @Test
    fun testBinaryOperations() {
        val or = """
            |println(5 || 3)
            |println(0 || 0)
        """.trimMargin()
        assertEquals("1\n0\n", runTest(or))

        val and = """
            |println(5 && 3)
            |println(5 && 0)
            |println(5 && 3 && 1)
        """.trimMargin()
        assertEquals("1\n0\n1\n", runTest(and))

        val module = """
            |println(5 % 3)
            |println(5 % 1)
            |println(5 % 3 % 2)
        """.trimMargin()
        assertEquals("2\n0\n0\n", runTest(module))

        val div = """
            |println(5 / 3)
            |println(7 / 1)
            |println(7 / 3 / 2)
        """.trimMargin()
        assertEquals("1\n7\n1\n", runTest(div))
    }

    @Test
    fun testSamples() {
        val sample1 = """
            |var a = 10
            |var b = 20
            |if (a > b) {
            |   println(1)
            |} else {
            |   println(0)
            |}
        """.trimMargin()
        assertEquals("0\n", runTest(sample1))

        val sample2 = """
            |fun fib(n) {
            |   if (n <= 1) {
            |       return 1
            |   }
            |   return fib(n - 1) + fib(n - 2)
            |}
            |
            |var i = 1
            |while (i <= 5) {
            |   println(i, fib(i))
            |   i = i + 1
            |}
        """.trimMargin()
        assertEquals("1 1\n2 2\n3 3\n4 5\n5 8\n", runTest(sample2))

        val sample3 = """
            |fun foo(n) {
            |   fun bar(m) {
            |       return m + n
            |   }
            |   return bar(1)
            |}
            |
            |println(foo(41)) // prints 42
        """.trimMargin()
        assertEquals("42\n", runTest(sample3))
    }

    private fun runTest(program: String): String {
        val writer = StringWriter()
        runInterpreter(CharStreams.fromString(program), writer)
        return writer.toString()
    }
}