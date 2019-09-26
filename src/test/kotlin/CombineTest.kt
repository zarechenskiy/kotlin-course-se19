package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class CombineTest {

    @Test
    fun testRemoveComments() {
        val code = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            //
            var i = 1 // one more // double comment
            while (i <= 10) {
                println(i, fib(i))
                i = i + 1 // comment
            }
            """.trimIndent()
        val expectedCode = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            
            var i = 1 
            while (i <= 10) {
                println(i, fib(i))
                i = i + 1 
            }
        """.trimIndent()
        val foundCode = removeComments(
            code.split(System.lineSeparator())
        ).joinToString(separator = System.lineSeparator())
        assertEquals(expectedCode, foundCode)
    }

    private fun getStdout(code: String): List<String> {
        ExecutionContext.stdOut.clear()
        val lexer = LangLexer(CharStreams.fromString(code))
        val parser = LangParser(CommonTokenStream(lexer))
        parser.file().block().accept(BlockVisitor())
        return ExecutionContext.stdOut
    }

    @Test
    fun ifScenario() {
        val code = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("0")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun fibScenario() {
        val code = """
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
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("1 1", "2 2", "3 3", "4 5", "5 8")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun nestedFunScenario() {
        val code = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
            
                return bar(1)
            }
            
            println(foo(41))
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("42")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun complexIfScenario() {
        val code = """
            fun f() {
                var a = 10
                if ((50 < a && a < 100) || (a % 10 == 0)) {
                    return 1
                }
            }
            
            println(f())
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("1")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun complexMathExpressionScenario() {
        val code = """
            var a = 10
            println((a + 1) / (a - 1) * 101 % 3)
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("2")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun multiplyVarDefinition() {
        val code = """
            var a = 10
            if (a > 5) {
                a = a + 1
                println(a)
                var a = 20
                println(a)
                a = a * 10
                println(a)
            }
            println(a)
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("11", "20", "200", "11")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun multiplyFunDefinition() {
        val code = """
            fun sqr(a, b, c) {
                return a * a + b * b + c * c
            }
            
            var a = 10
            
            println(sqr(1, 2, 3))
            
            if (a % 3 == 1) {
                fun sqr(a, b) {
                    return a * a + b * b
                }
                println(sqr(1, 2))
            }
            
            println(sqr(1, 10, 20))
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("14", "5", "501")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    @Test
    fun multiplyVarDefInFun() {
        val code = """
            var a = 10
            fun f(a) {
                a = 2
                println(a)
                return a * a
            }
            
            println(f(a))
            println(a)
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("2", "4", "10")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

    private fun checkExceptionThrown(code: String): Boolean {
        return try {
            getStdout(code)
            false
        } catch (e: Exception) {
            true
        }
    }

    @Test
    fun multiplyVarDefError() {
        val code = """
            var a = 10
            a = 20
            var a = 100
        """.trimIndent()
        assertTrue(checkExceptionThrown(code))
    }

    @Test
    fun varAndFunEqualNamesError() {
        val code1 = """
            fun a() {
            }
            var a = 10
        """.trimIndent()
        assertTrue(checkExceptionThrown(code1))
        val code2 = """
            var a = 10
            fun a() {
            }
        """.trimIndent()
        assertTrue(checkExceptionThrown(code2))
    }

    @Test
    fun multiplyFunDefError() {
        val code = """
            fun a() {
            }
            fun a(x, y) {
            }
        """.trimIndent()
        assertTrue(checkExceptionThrown(code))
    }

    @Test
    fun functionAndArgWithEqualNamesError() {
        val code = """
            fun a(a, y) {
            }
            println(a(1,2))
        """.trimIndent()
        assertTrue(checkExceptionThrown(code))
    }

    @Test
    fun noReturnFun() {
        val code = """
            fun a() {
                println(12)
            }
            println(a())
        """.trimIndent()

        val foundStdout = getStdout(code)
        val expectedStdout = listOf("12", "0")

        assertArrayEquals(expectedStdout.toTypedArray(), foundStdout.toTypedArray())
    }

}
