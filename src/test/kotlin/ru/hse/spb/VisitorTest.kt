package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import java.util.stream.Stream


internal class VisitorTest : LanguageTest() {

    @Test
    fun shouldPrintOne() {
        val program = """
            |println(1)
        """.trimMargin()

        createParser(program).file().accept(Visitor())

        assertEquals("1$lineBeaker", myOut.toString())
    }

    @ParameterizedTest
    @MethodSource("expressionTestProvider")
    fun shouldCalculateAndPrintAnswer(program: String, answer: Int) {

        createParser(program).file().accept(Visitor())

        assertEquals("$answer$lineBeaker", myOut.toString())
    }

    @ParameterizedTest
    @MethodSource("realTestProvider")
    fun shouldInterpretAndPrintAnswers(program: String, answer: Int) {

        createParser(program).file().accept(Visitor())

        assertEquals("$answer$lineBeaker", myOut.toString())
    }

    @Test
    fun shouldInterpretFib() {
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
            """.trimIndent()

        val ans = listOf(1 to 1, 2 to 2, 3 to 3, 4 to 5, 5 to 8)
        createParser(program).file().accept(Visitor())

        assertEquals(ans.joinToString(lineBeaker) { (a, b) -> "$a $b" } + lineBeaker, myOut.toString())
    }

    @Test
    fun shouldThrowException() {
        assertThrows<InterpreterException> {
            Interpreter.run("fun } return 0 {")
        }
    }

    @Test
    fun shouldThrowExceptionWhenVarNotDefined() {
        val program = """
            |var a = 1
            |print a + b
        """.trimMargin()

        val e = assertThrows<InterpreterException> {
            Interpreter.run(program)
        }
        assertEquals(2, e.line)
    }

    companion object {

        fun createParser(program: String): FunCallParser {
            val lexer = FunCallLexer(CharStreams.fromString(program))
            return FunCallParser(CommonTokenStream(lexer))
        }

        @JvmStatic
        fun realTestProvider(): Stream<Arguments> =
                Stream.of(
                        Arguments.of(
                                """
                                    var a = 10
                                    var b = 20
                                    println(a + b + 12)
                                """.trimIndent(),
                                42
                        ),
                        Arguments.of(
                                """
                                    var a = 10
                                    var b = 20
                                    if (a > b) {
                                        println(1)
                                    } else {
                                        println(0)
                                    }
                                    
                                """.trimIndent(),
                                0
                        ),
                        Arguments.of(
                                """
                                    fun foo(n) {
                                        fun bar(m) {
                                            return m + n
                                        }
                                    
                                        return bar(1)
                                    }
                                    
                                    println(foo(41))
                                """.trimIndent(),
                                42
                        )

                )
        @JvmStatic
        fun expressionTestProvider(): Stream<Arguments> =
                Stream.of(
                        Arguments.of(
                                """
                                    |println(7 + 35)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println(100 - 58)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println(3 * 14)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println(128 / 3)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println(128 % 43)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println(128 < 42)
                                """.trimMargin(),
                                0
                        ),
                        Arguments.of(
                                """
                                    |println(128 > 42)
                                """.trimMargin(),
                                1
                        ),
                        Arguments.of(
                                """
                                    |println(42 <= 42)
                                """.trimMargin(),
                                1
                        ),
                        Arguments.of(
                                """
                                    |println(42 < 42)
                                """.trimMargin(),
                                0
                        ),
                        Arguments.of(
                                """
                                    |println(6 + 6 * 6)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println((7 + 7) * 3)
                                """.trimMargin(),
                                42
                        ),
                        Arguments.of(
                                """
                                    |println((7 + 7) * 3 == 7 * 3 + 7 * 3)
                                """.trimMargin(),
                                1
                        ),
                        Arguments.of(
                                """
                                    |println((7 + 7) * 3 == 42 && 7 * 3 + 7 * 3 != 43)
                                """.trimMargin(),
                                1
                        ),
                        Arguments.of(
                                """
                                    |println(1 || 0 && 0 )
                                """.trimMargin(),
                                1
                        ),
                        Arguments.of(
                                """
                                    |println((1 || 0) && 0 )
                                """.trimMargin(),
                                0
                        )
                )
    }
}