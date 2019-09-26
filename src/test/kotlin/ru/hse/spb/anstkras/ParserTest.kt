package ru.hse.spb.anstkras

import org.antlr.v4.runtime.CharStreams
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import ru.hse.spb.parser.FunInterpreterLexer
import ru.hse.spb.parser.FunInterpreterParser

class ParserTest {
    @Test
    fun testVariableAndExpression() {
        val program = """
            var a = (10 + 3) > 14 && 1
        """
                .trimIndent()
        val parser = getParser(program)
        val variable = parser.file().block().statement(0).variable()
        assertEquals("a", variable.IDENTIFIER().text)
        val logicExpr = variable.expression().logicExpr()
        assertEquals("&&", logicExpr.LOGIC_OPS(0).text)
        val compareExpr = logicExpr.compareExpr()
        assertEquals(">", compareExpr.COMP_OPS().text)
        assertEquals("(10+3)", compareExpr.expr(0).text)
        assertEquals("14", compareExpr.expr(1).term().factor().literal().text)
        assertEquals("1", logicExpr.logicExpr(0).compareExpr().text)
    }

    @Test
    fun testIfStatement() {
        val program = """
            if (0) {
                return 1
            } else {
                return 2
            }
        """
                .trimIndent()
        val parser = getParser(program)
        val ifStatement = parser.file().block().statement(0).ifStatement()
        assertEquals("0", ifStatement.ifExpr.text)
        assertEquals("return1", ifStatement.trueBlock.block().text)
        assertEquals("return2", ifStatement.falseBlock.block().text)
    }

    @Test
    fun testFunction() {
        val program = """
            fun foo(n) {
                return n + 1
            }
        """
                .trimIndent()
        val parser = getParser(program)
        val function = parser.file().block().statement(0).function()
        assertEquals("foo", function.IDENTIFIER().text)
        assertEquals("n+1", function.blockWithBraces().block().statement(0).returnStatement().expression().text)
    }

    private fun getParser(program : String) : FunInterpreterParser {
        return FunInterpreterParser(
                org.antlr.v4.runtime.CommonTokenStream(
                        FunInterpreterLexer(
                                CharStreams.fromString(program)
                        )
                )
        )
    }
}