package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert
import org.junit.Test
import ru.hse.spb.parser.MyLangLexer
import ru.hse.spb.parser.MyLangParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestExecution {
    private fun execute(code: String): String {
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))

        MyLangVisitor().visitLFile(MyLangParser(BufferedTokenStream(MyLangLexer(CharStreams.fromString(code)))).lFile())
        return out.toString()
    }

    @Test
    fun `test variable declaration`() {
        val result = execute("""
            var a = 1
            var b = 2
        """.trimIndent())
        Assert.assertEquals("", result)
    }


    @Test
    fun `test variable declaration and comments`() {
        val result = execute("""
            var a = 1
            //fst comment
            var b = 2
            //snd comment
        """.trimIndent())
        Assert.assertEquals("", result)
    }

    @Test
    fun `test variable declaration and print`() {
        val result = execute("""
            var a = 1
            var b = 2
            println(a + b)
        """.trimIndent())
        Assert.assertEquals("3\n", result)
    }

    @Test
    fun `test integer variables`() {
        val result = execute("""
            var a = 10
            var b = 15
            var c = 3
            var d = 5
            println(a / d + b % c + a * d - (c * d + b + d) - 10)
        """.trimIndent())
        Assert.assertEquals("7\n", result)
    }

    @Test
    fun `test bool variables`() {
        val result = execute("""
            var a = false
            var b = true
            var c = false
            println(a && b || c || true)
            println(a && b || c )
        """.trimIndent())
        Assert.assertEquals("true\nfalse\n", result)
    }

    @Test
    fun `test simple fun`() {
        val result = execute("""
            fun fib(a) {
                println(a)
            }
            
            var a = 1
            fib(a)
        """.trimIndent())
        Assert.assertEquals("1\n", result)
    }

    @Test
    fun `test simple closure fun`() {
        val result = execute("""
            var a = 1

            fun fib() {
                println(a)
            }
            
            fib()
        """.trimIndent())
        Assert.assertEquals("1\n", result)
    }

    @Test
    fun `test simple closure write`() {
        val result = execute("""
            var a = 1

            fun fib() {
                a = 2
            }
            
            fib()
            println(a)
        """.trimIndent())
        Assert.assertEquals("2\n", result)
    }

    @Test
    fun `test simple closure override`() {
        val result = execute("""
            var a = 1

            fun fib() {
                var a = 2
            }
            
            fib()
            println(a)
        """.trimIndent())
        Assert.assertEquals("1\n", result)
    }

    @Test
    fun `test complex closure `() {
        val result = execute("""
            var a = 1
            
            fun first() {
                var b = a + 5
                
                fun second() {
                    var c = b + 4
                    return c
                }
                
                return second()
            }
            
            println(first())
        """.trimIndent())
        Assert.assertEquals("10\n", result)
    }
}
