package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.parser.FunInterpreterLexer
import ru.hse.spb.parser.FunInterpreterParser

class TestSource {
    private class AssertFunction<T> : Function<Int> {
        private val expected = "expected"
        private val actual = "actual"

        override fun compute(state: State): Int {
            assertEquals(state.getVariable(expected), state.getVariable(actual))
            return 0
        }

        override fun argsList(): List<String> {
            return listOf(expected, actual)
        }

    }

    private fun testResult(code: String) {
        val lexer = FunInterpreterLexer(CharStreams.fromString(code))
        val parser = FunInterpreterParser(CommonTokenStream(lexer))
        parser.file().accept(StatementsEvaluatorVisitor(
                mapOf("assertEquals" to AssertFunction<Int>()))
        )
    }

    @Test
    fun testSimpleCompute() {
        testResult("""
            |assertEquals(10, 5+5)
            |assertEquals(20, 10+5*2)
            |assertEquals(0, 5*5-25)
        """.trimMargin())
    }

    @Test
    fun testWhile() {
        testResult("""
            |var a = 0
            |var b = 0
            |while(a < 10) {
            |    a = a+1
            |    b = b+1
            |}
            |assertEquals(10, b)
        """.trimMargin())
    }

    @Test
    fun testIf() {
        testResult("""
            |var a = 0
            |var b = 10
            |if(a < 10) {
            |    assertEquals(1, 1)
            |} else {
            |   assertEquals(0, 1)
            |}
            |if(a > 10) {
            |   assertEquals(0, 1)
            |} else {
            |   assertEquals(1, 1)
            |}
        """.trimMargin())
    }

    @Test
    fun testFib() {
        testResult("""
            |fun fib(a) {
            |   if(a==0) {
            |       return 0
            |   }
            |   if(a == 1 || a==2) {
            |       return 1
            |   }
            |   return fib(a-1) + fib(a-2) 
            |}
            |assertEquals(0, fib(0))
            |assertEquals(1, fib(1))
            |assertEquals(1, fib(2))
            |assertEquals(2, fib(3))
            |assertEquals(3, fib(4))
            |assertEquals(5, fib(5))
            |assertEquals(8, fib(6))
            |assertEquals(13, fib(7))
        """.trimMargin())
    }

    @Test
    fun testInnerFunc() {
        testResult("""
            |fun foo(n) {
            |    fun bar(m) {
            |        return m + n
            |    }
            |    return bar(1)
            |}
            |assertEquals(42, foo(41))
            """.trimMargin())
    }

    @Test
    fun testComments() {
        testResult("""
            |fun foo(n) {
            |    fun bar(m) {
            |        return m + n
            |    }
            |    return bar(1)
            |}
            |// assertEquals(1, 0)
            |assertEquals(42, foo(41))
            """.trimMargin())
    }

    @Test
    fun testParser() {
        val code = """
            |fun function(a, b) {
            |   return a
            |}
            |
            |var a = 10
            |if (a > 20) {
            |    println(1)
            |} else {
            |   println(2)
            |}
            |while(a > 0) {
            |   println(3)
            |}
        """.trimMargin()

        val lexer = FunInterpreterLexer(CharStreams.fromString(code))
        val parser = FunInterpreterParser(CommonTokenStream(lexer))
        val root = parser.file().getChild(0)
        val function = root.getChild(0).getChild(0)
        assertTrue(function is FunInterpreterParser.FunctionContext)
        assertEquals(2,
                (function as FunInterpreterParser.FunctionContext)
                        .functionArgumentNames().parameters.size
        )


        val varStatement = root.getChild(2).getChild(0)
        assertTrue(varStatement is FunInterpreterParser.VariableAssignContext)

        val ifStatement = root.getChild(4).getChild(0)
        assertTrue(ifStatement is FunInterpreterParser.IfStatementContext)

        val whileStatement = root.getChild(6).getChild(0)
        assertTrue(whileStatement is FunInterpreterParser.WhileStatementContext)
    }
}