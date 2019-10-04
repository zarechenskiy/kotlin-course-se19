package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.parser.FunPLLexer
import ru.hse.spb.parser.FunPLParser
import java.lang.IllegalStateException

val program1 = """
    |var a = 10
    |var b = 20
    |if (a > b) {
        |println(1)
    |} else {
        |println(0)
    |}
""".trimMargin()

val program2 = """
    |fun fib(n) {
        |if (n <= 1) {
            |return 1
        |}
    |return fib(n - 1) + fib(n - 2)
    |}

    |var i = 1
    |while (i <= 5) {
        |println(i, fib(i))
        |i = i + 1
    |}

""".trimMargin()

val program3 = """
    fun fib(n) {
        if (n <= 1) {
            return 1
        }
        return fib(n - 1) + fib(n - 2)
    }
    return (fib(1) + fib(2))
"""

val program4 = """
    var a = 1
    if (1) {
        if (2) {
            if (3) {
                return a
            }
        }
    }
""".trimIndent()

val program5 = """
    var a = 1
    if (1) {
        if (2) {
            if (3) {
                return b
            }
        }
    }
""".trimIndent()

class TestSource {
    @Test
    fun testNumStatements() {
        val lexer = FunPLLexer(CharStreams.fromString(program1))
        val parser = FunPLParser(CommonTokenStream(lexer))
        assertEquals(3, parser.file().block().statement().size)
    }

    @Test
    fun testStatementTypes() {
        val lexer = FunPLLexer(CharStreams.fromString(program2))
        val parser = FunPLParser(CommonTokenStream(lexer))
        val statements = parser.file().block().statement()
        assertNotNull(statements[0].function())
        assertNotNull(statements[1].variable())
        assertNotNull(statements[2].whileExp())
    }

    @Test
    fun testOuterVariable() {
        val lexer = FunPLLexer(CharStreams.fromString(program4))
        val parser = FunPLParser(CommonTokenStream(lexer))
        assertEquals(1, parser.file().accept(StatementsEvaluationVisitor()))
    }

    @Test
    fun testArithmetic() {
        val lexer = FunPLLexer(CharStreams.fromString("return 2 + 2 * 2"))
        val parser = FunPLParser(CommonTokenStream(lexer))
        assertEquals(6, parser.file().accept(StatementsEvaluationVisitor()))
    }

    @Test
    fun testValue1() {
        val lexer = FunPLLexer(CharStreams.fromString(program1))
        val parser = FunPLParser(CommonTokenStream(lexer))
        assertEquals(0, parser.file().accept(StatementsEvaluationVisitor()))
    }

    @Test
    fun testValue3() {
        val lexer = FunPLLexer(CharStreams.fromString(program3))
        val parser = FunPLParser(CommonTokenStream(lexer))
        val visitor = StatementsEvaluationVisitor()
        assertEquals(3, parser.file().accept(visitor))
    }

}