package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestSource {
    private fun validate(code: String, output: String) {
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))

        EvalVisitor().visitFile(ExpParser(BufferedTokenStream(ExpLexer(CharStreams.fromString(code)))).file())
        assertEquals(output, out.toString().replace("\r\n", "\n").replace('\r', '\n'))

        System.setOut(System.out)
    }

    @Test
    fun `test variable declaration`() {
        validate("""
            var a = 10
            if (a > 5) {
                var a = 15
                println(a)
            }
            println(a)
        """.trimIndent(), "15\n10\n")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test variable declaration with same name`() {
        validate("""
            var a = 10
            var a = 15
        """.trimIndent(), "")
    }

    @Test
    fun `test function declaration`() {
        validate("""
            fun func(n) {
                println(n)
                fun func(n) {
                    println(42)
                }
                
                func(5)
            }
            
            func(1)
        """.trimIndent(), "1\n42\n")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test function declaration with same name`() {
        validate("""
            fun fib(n) {
            
            }
            
            fun fib(a) {
            
            }
        """.trimIndent(), "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test function illegal argument count`() {
        validate("""
            fun fib(a, b, c) {
            
            }
            
            fib(4,   5,6,7)
        """.trimIndent(), "")
    }

    @Test
    fun `test condition`() {
        validate("""
            if (0 > 5) {
                println(1)
            } else {
                println(0)
            }
            
            if (5 == 5) {
                println(1)
            }
        """.trimIndent(), "0\n1\n")
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `test illegal argument condition`() {
        validate("""
            if (5 * 3) {
            
            }
        """.trimIndent(), "-1\n")
    }

    @Test
    fun `test while`() {
        validate("""
            var x = 5
            while (x >= 0) {
                x = x - 1
            }
            
            while (x >= 0) {
                x = 42
            }
            
            println(x)
        """.trimIndent(), "-1\n")
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `test illegal argument while`() {
        validate("""
            var x = 5
            while (x * 5) {
                x = x - 1
            }
        """.trimIndent(), "-1\n")
    }

    @Test
    fun `test logical operators`() {
        validate("""
            println(false || (false || true) && true)
        """.trimIndent(), "true\n")
    }

    @Test
    fun `test math operators`() {
        validate("""
            println((2 + 2 * 2) % (9 / 3))
        """.trimIndent(), "0\n")
    }

    @Test
    fun smoke1() {
        validate("""
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent(), "0\n")
    }

    @Test
    fun smoke2() {
        validate("""
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
        """.trimIndent(), "1 1\n2 2\n3 3\n4 5\n5 8\n")
    }

    @Test
    fun smoke3() {
        validate("""
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
            
                return bar(1)
            }
            
            println(foo(41)) // prints 42
        """.trimIndent(), "42\n")
    }
}
