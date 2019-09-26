package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.parser.GrammarLexer
import ru.hse.spb.parser.GrammarParser
import java.lang.IllegalStateException

class TestSource {


    @Test
    fun testNumber() {
        assertEquals(3, "3".evaluateExpression())
        assertEquals(0, "0".evaluateExpression())
        assertEquals(-100, "-100".evaluateExpression())
    }

    @Test
    fun testSum() {
        assertEquals(3, "1 + 2".evaluateExpression())
        assertEquals(0, "1 + -1".evaluateExpression())
        assertEquals(42, "21 + 2 + 4 + 2 + 13".evaluateExpression())
    }

    @Test
    fun testMultiplication() {
        assertEquals(6, "3 * 2".evaluateExpression())
        assertEquals(0, "100 * 0".evaluateExpression())
        assertEquals(42, "2 * 3 * 7".evaluateExpression())
    }

    @Test
    fun testSubtraction() {
        assertEquals(8, "10 - 2".evaluateExpression())
        assertEquals(0, "1 - 1".evaluateExpression())
        assertEquals(-9, "1 - 10".evaluateExpression())
        assertEquals(-42, "-21 - 2 - 4 - 2 - 13".evaluateExpression())
    }

    @Test
    fun testDivision() {
        assertEquals(0, "0 / 10".evaluateExpression())
        assertEquals(0, "1 / 10".evaluateExpression())
        assertEquals(1, "10 / 10".evaluateExpression())
        assertEquals(1, "16 / 10".evaluateExpression())
        assertEquals(1, "11 / 10".evaluateExpression())
        assertEquals(-1, "-11 / 10".evaluateExpression())
    }

    @Test
    fun testRelationalOperations() {
        assertEquals(1, "11 > 10".evaluateExpression())
        assertEquals(1, "11 >= 10".evaluateExpression())
        assertEquals(0, "11 < 10".evaluateExpression())
        assertEquals(0, "11 <= 10".evaluateExpression())
        assertEquals(1, "-1 <= 10".evaluateExpression())
        assertEquals(1, "-1 < 10".evaluateExpression())
        assertEquals(0, "-1 > 10".evaluateExpression())
        assertEquals(0, "-1 >= 10".evaluateExpression())
    }

    @Test
    fun testEqualityOperations() {
        assertEquals(1, "-1 != 10".evaluateExpression())
        assertEquals(0, "-1 == 10".evaluateExpression())
        assertEquals(1, "10 == 10".evaluateExpression())
        assertEquals(0, "10 != 10".evaluateExpression())
    }

    @Test
    fun testLogicalOperations() {
        assertEquals(1, "1 && 1".evaluateExpression())
        assertEquals(0, "1 && 0".evaluateExpression())
        assertEquals(0, "0 && 1".evaluateExpression())
        assertEquals(0, "0 && 0".evaluateExpression())

        assertEquals(1, "1 || 1".evaluateExpression())
        assertEquals(1, "1 || 0".evaluateExpression())
        assertEquals(1, "0 || 1".evaluateExpression())
        assertEquals(0, "0 || 0".evaluateExpression())

        assertEquals(1, "0 || 42".evaluateExpression())
        assertEquals(1, "39 && 42".evaluateExpression())
    }

    @Test
    fun testExpressions() {
        assertEquals(1, "0 >= 5 || 3 <= 7".evaluateExpression())
        assertEquals(0, "0 >= 5 || 3 >= 7".evaluateExpression())

        assertEquals(17, "3 + 6 * 8 + (2 * 4 - 6 * 7)".evaluateExpression())
        assertEquals(-212, "(15 - 32) * (34 + 16) / 4".evaluateExpression())
    }

    @Test
    fun testIf() {
        val program1 = """
        |if (10 > 20) {
        |    return 1
        |} else {
        |    return 0
        |}
        """.trimMargin()
        assertEquals(0, program1.evaluateFile())
        val program2 = """
        |if (10 < 20) {
        |    return 1
        |} else {
        |    return 0
        |}
        """.trimMargin()
        assertEquals(1, program2.evaluateFile())
    }

    @Test
    fun testFunction() {
        val program1 = """
            |fun fib(n) {
            |   if (n <= 1) {
            |       return 1
            |   }
            |   return fib(n - 1) + fib(n - 2)
            |}
            |return fib(10)
        """.trimMargin()
        assertEquals(89, program1.evaluateFile())
        val program2 = """
            |fun foo() {
            |   return 42
            |}
            |
            |fun boo(n) {
            |   return n + 4
            |}
            |
            |return boo(foo())
        """.trimMargin()
        assertEquals(46, program2.evaluateFile())
    }

    @Test
    fun testFunctions() {
        val program = """
            |fun foo(n) {
            |   fun bar(m) {
            |       return m + n
            |   }
            |
            |   return bar(1)
            |}
            |return foo(41)
        """.trimMargin()
        assertEquals(42, program.evaluateFile())
    }

    @Test
    fun testVariables() {
        val program1 = """
            |var value = 2
            |value = value + 2
            |return value + 2
        """.trimMargin()
        assertEquals(6, program1.evaluateFile())
        val program2 = """
            |var variable = 2
            |variable = variable + 2
            |return variable + 2
        """.trimMargin()
        assertEquals(6, program2.evaluateFile())

        val program3 = """
            |var a = 3
            |if (1) {
            |   var a = 4
            |   return a
            |}
        """.trimMargin()
        assertEquals(4, program3.evaluateFile())
    }

    @Test
    fun testAssignment() {
        val program = """
            |var a = 3
            |var b
            |var c = 12 / 4
            |b = a + c
            |c = b + 3
            |return a + b + c
            |}
        """.trimMargin()
        assertEquals(18, program.evaluateFile())
    }

    @Test
    fun testWhile() {
        val program1 = """
            |var i = 0
            |while (i < 10) {
            |   i = i + 1
            |}
            |return i
        """.trimMargin()
        assertEquals(10, program1.evaluateFile())

        val program2 = """
            |var i = 0
            |while (i < 10) {
            |   i = i + 1
            |   if (i > 5) {
            |       return -1
            |   }
            |}
            |return i
        """.trimMargin()
        assertEquals(-1, program2.evaluateFile())
    }

    @Test
    fun testExpression() {
        val program = """
            |var x = 10
            |var y = 20
            |
            |fun sum(a, b) {
            |   return a + b
            |}
            |
            |fun sum2(a, b) {
            |   return sum(a, b) % 2
            |}
            |
            |x = sum(x, y)
            |return x * 10 - y * sum(2, 3) + sum2(x, y)
        """.trimMargin()
        assertEquals(200, program.evaluateFile())
    }

    @Test(expected = IllegalStateException::class)
    fun testVariableRedefinition() {
        val program = """
            |var x = 3
            |var x = 4
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testFunctionRedefinition() {
        val program = """
            |fun foo() { }
            |fun foo() { }
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testUndefinedVariable() {
        val program = """
            |return x + y
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testUndefinedFunction() {
        val program = """
            |foo(12)
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testIllegalArgumentsNumber() {
        val program = """
            |fun foo(x, y) {
            |   return x + y
            |}
            |
            |return foo(3, 4, 5)
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testUndefinedAssignmentVariable() {
        val program = """
            |a = 48
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testUndefinedValueVariable() {
        val program = """
            |var a
            |return a
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testVariableScope() {
        val program = """
            |if (1) {
            |   var x = 3 
            |}
            |return x
        """.trimMargin()
        program.evaluateFile()
    }

    @Test(expected = IllegalStateException::class)
    fun testFunctionScope() {
        val program = """
            |fun foo() {
            |   fun boo() {
            |       return 42
            |   }
            |   
            |   return 73
            |}
            |
            |return boo()
        """.trimMargin()
        program.evaluateFile()
    }

    @Test
    fun testDefaultReturnValue() {
        val program = """
            |var x = 3
            |fun foo() {
            |   x = x + 1
            |}
            |
            |return foo()
            
        """.trimMargin()
        assertEquals(0, program.evaluateFile())
    }

    @Test
    fun testPrintln() {
        val program = """
            |println(42)
        """.trimMargin()
        program.evaluateFile()
    }

    @Test
    fun testComments() {
        val program = """
            |var x = 0 // + 32
            |if (x == 0) {
            |   return 1
            |}
            |return 0
        """.trimMargin()
        assertEquals(1, program.evaluateFile())
    }

}

private fun parseString(code: String): GrammarParser {
    return GrammarParser(CommonTokenStream(GrammarLexer(CharStreams.fromString(code))))
}

private fun GrammarParser.evaluateFile(): Int {
    return file().accept(EvaluationVisitor())
}

private fun GrammarParser.evaluateExpression(): Int {
    return expression().accept(EvaluationVisitor())
}

private fun String.evaluateExpression(): Int {
    return parseString(this).evaluateExpression()
}

private fun String.evaluateFile(): Int {
    return parseString(this).evaluateFile()
}