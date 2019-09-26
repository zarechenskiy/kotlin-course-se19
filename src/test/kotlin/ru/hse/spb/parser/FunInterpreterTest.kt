package ru.hse.spb.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.*
import org.junit.Test
import java.lang.Exception
import java.lang.IllegalStateException

class FunInterpreterTest {
    fun returnResult(program: String): Int {
        val lexer = FunLangLexer(CharStreams.fromString(program))
        val tokens = CommonTokenStream(lexer)
        tokens.fill()
        val parser = FunLangParser(tokens)
        return FunInterpreter().visit(parser.file()).value
    }

    @Test
    fun bigTest() {
        val program = """
            var a = 0
            var b = a

            if (1) {
                b = b + 1
            } else {
                return 0
            }

            fun inc(a) {
                return a + 1
            }

            fun fib(n) {
                if (n <= 0) {
                    return 0
                }
                if (n == 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }

            fun strangeFunc(n) {
                fun first(n) {
                    if (((n) == 0)) {
                        return 0
                    }
                    return second(n - 1) + 1
                }

                fun second(n) {
                    if (n == 0) {
                        return 0
                    }
                    return first(n - 1) + 1
                }

                return first(n)
            }

            fun hiding(n) {
                fun fib(x) {
                    return x
                }
                return fib(n)
            }

            var result
            result = strangeFunc(inc(a + b))
            return result + fib(4) + hiding(10) + (2 + 3 * 2 || 1)
        """.trimIndent()
        assertEquals(16, returnResult(program))
    }

    @Test
    fun priorityTest() {
        val program = """
            return (3 + 1 && 0) + (5 % 3 * 2 / 4) * 3
        """.trimIndent()
        assertEquals(3, returnResult(program))
    }

    @Test
    fun returnTest() {
        val program = """
            fun low(n) {
                fun dec(n) {
                    return n - 1
                }

                while (1) {
                    if (n % 11 == 0) {
                        return n
                    }
                    n = dec(n)
                }
            }

            return low(70)
        """.trimIndent()
        assertEquals(66, returnResult(program))
    }

    @Test
    fun exceptionTest() {
        val program = """
            fun fib() {
            }

            fun fib(n) {
                return n
            }

            return 0
        """.trimIndent()
        var caught: Exception? = null
        try {
            returnResult(program)
        } catch (e: IllegalStateException) {
            caught = e
        }
        assertNotNull(caught)
    }
}