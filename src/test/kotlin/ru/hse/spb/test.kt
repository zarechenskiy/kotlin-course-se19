package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser

class TestSource {
    @Test
    fun testParser() {
        val program= """
            |fun min(x,y) {
            |   if (x<y) {
            |       return x
            |   } else {
            |       return y
            |   }
            |}
            |var a = 2
            |println(a, 4)
        """.trimMargin()

        val lexer = FunCallLexer(CharStreams.fromString(program))
        val parser = FunCallParser(BufferedTokenStream(lexer))
        val ast = parser.file().block()

        val func = ast.statement(0).func()
        val varrr = ast.statement(1).`var`()
        val print = ast.statement(2).expr()

        assertTrue(func is FunCallParser.FuncContext)
        assertTrue(varrr is FunCallParser.VarContext)
        assertTrue(print is FunCallParser.PrintExprContext)
        assertTrue(func.brBlock().block().statement(0).ifSt() is FunCallParser.IfStContext)

        assertEquals(2, varrr.expr().text.toInt())
    }

    @Test
    fun testInterpreter() {
        val program= """
            |fun min(x,y) {
            |   if (x<y) {
            |       return x
            |   } else {
            |       return y
            |   }
            |}
            |var a = 2
            |println(min(a, 4))
        """.trimMargin()

        val lexer = FunCallLexer(CharStreams.fromString(program))
        val parser = FunCallParser(BufferedTokenStream(lexer))
        assertEquals("2", parser.file().accept(StatementsEvaluationVisitor()))
    }
}

