package ru.hse.spb.anstkras

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.IllegalStateException

class IntegrationTest {

    @Test
    fun testFirst() {
        val program = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """
                .trimIndent()
        checkOutput(program, "0\n")
    }

    @Test
    fun testSecond() {
        val program = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            
            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """
                .trimIndent()
        checkOutput(program, "1 1\n2 2\n3 3\n4 5\n5 8\n")
    }

    @Test
    fun testThird() {
        val program = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
            
                return bar(1)
            }
            
            println(foo(41))
        """
                .trimIndent()
        checkOutput(program, "42\n")
    }

    @Test
    fun testComment() {
        val program = """
            // println(1)
            println(2)
            // println(1)
        """.trimIndent()
        checkOutput(program, "2\n")
    }

    @Test
    fun testVariablesCollision() {
        val program = """
            var a = 1
            fun foo(a) {
                println(a)
            }
            foo(2)
        """.trimIndent()
        checkOutput(program, "2\n")
    }

    @Test
    fun functionOverride() {
        val program = """
            fun foo(a, b) {
                println(a + b)
            }
            fun bar(n) {
                fun foo(a, b) {
                    println(a - b)
                }
                foo(n, n)
            }
            bar(1)
        """.trimIndent()
        checkOutput(program, "0\n")
    }

    @Test
    fun testTryOverload() {
        val program = """
            fun foo(a) {
                return a
            }
            fun foo(a, b) {
                return a + b
            }
        """.trimIndent()
        checkThrows(program)
    }

    @Test
    fun testTryUnDefinedVariable() {
        val program = """
            var a = b
        """.trimIndent()
        checkThrows(program)
    }

    private fun checkOutput(program: String, expected: String) {
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))
        runProgram(program)
        assertEquals(expected, out.toString())
    }

    private fun checkThrows(program: String) {
        assertThrows<IllegalStateException> { runProgram(program) }
    }
}