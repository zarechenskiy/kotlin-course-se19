package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.hse.spb.FunLanguageVisitorImplementation.InterpretingException
import ru.hse.spb.parser.FunLanguageLexer
import ru.hse.spb.parser.FunLanguageParser
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.*

class ParserAndInterpreterTest {
    @Test
    fun testSample1() {
        assertEquals(SAMPLE_1_ANSWER, getInterpreterOutput(SAMPLE_CODE_1))
    }

    @Test
    fun testSample2() {
        assertEquals(SAMPLE_2_ANSWER, getInterpreterOutput(SAMPLE_CODE_2))
    }

    @Test
    fun testSample3() {
        assertEquals(SAMPLE_3_ANSWER, getInterpreterOutput(SAMPLE_CODE_3))
    }

    @Test
    fun testScope() {
        assertEquals(SCOPE_TEST_ANSWER, getInterpreterOutput(SCOPE_TEST_CODE))
    }

    @Test
    fun testAssignmentAndExpression() {
        assertEquals(
                ASSIGNMENT_AND_EXPRESSIONS_ANSWER,
                getInterpreterOutput(ASSIGNMENT_AND_EXPRESSIONS_CODE)
        )
    }

    @Test
    fun testCodeWithErrors() {
        assertThrows(InterpretingException::class.java) {
            getInterpreterOutput(NO_VARIABLE_CODE)
        }

        assertThrows(InterpretingException::class.java) {
            getInterpreterOutput(NO_FUNCTION_CODE)
        }

        assertThrows(InterpretingException::class.java) {
            getInterpreterOutput(FUNCTION_HIDING_CODE)
        }
    }

    private fun getInterpreterOutput(code: String): String {
        val lexer = FunLanguageLexer(CharStreams.fromString(code))
        val parser = FunLanguageParser(CommonTokenStream(lexer))

        val byteArray = ByteArrayOutputStream()
        PrintWriter(byteArray).use {
            parser.file().accept(FunLanguageVisitorImplementation(it))
        }
        return byteArray.toString()
    }

    private companion object {
        private val SAMPLE_CODE_1 = """
            |var a = 10
            |var b = 20
            |if (a > b) {
            |    println(1)
            |} else {
            |    println(0)
            |}
        """.trimMargin()

        private const val SAMPLE_1_ANSWER = "0\n"

        private val SAMPLE_CODE_2 = """
            |fun fib(n) {
            |    if (n <= 1) {
            |        return 1
            |    }
            |    return fib(n - 1) + fib(n - 2)
            |}
            |
            |var i = 1
            |while (i <= 5) {
            |    println(i, fib(i))
            |    i = i + 1
            |}
        """.trimMargin()

        private val SAMPLE_2_ANSWER = StringJoiner("\n", "", "\n")
                .add("1").add("1")
                .add("2").add("2")
                .add("3").add("3")
                .add("4").add("5")
                .add("5").add("8")
                .toString()

        private val SAMPLE_CODE_3 = """
            |fun foo(n) {
            |    fun bar(m) {
            |        return m + n
            |    }
            |
            |    return bar(1)
            |}
            |
            |println(foo(41)) // prints 42
        """.trimMargin()

        private const val SAMPLE_3_ANSWER = "42\n"

        private val SCOPE_TEST_CODE = """
            |fun foo() {
            |   println(2)
            |}
            |
            |fun bar() {
            |   foo()
            |}
            |
            |fun main() {
            |   fun foo() {
            |       println(3)
            |   }
            |   bar()
            |}
            |
            |println(main())
        """.trimMargin()

        private const val SCOPE_TEST_ANSWER = "2\n0\n"

        private val ASSIGNMENT_AND_EXPRESSIONS_CODE = """
            |var a = 0
            |if ((2 + 2*2) + 3 / 3 == 7) {
            |   a = a + 1
            |}
            |
            |if (a <= a) {
            |   a = a + 1
            |}
            |
            |if (a + 2 != a) {a=a+1}
            |
            |println(a)
        """.trimMargin()

        private const val ASSIGNMENT_AND_EXPRESSIONS_ANSWER = "3\n"

        private const val NO_VARIABLE_CODE = "println(a)"

        private const val NO_FUNCTION_CODE = "function()"

        private val FUNCTION_HIDING_CODE = """
            |fun function() {
            |}
            |
            |var i = 10
            |while(i >= 1) {
            |   var function = 3
            |   function()
            |   i = i-1
            |}
        """.trimMargin()
    }
}