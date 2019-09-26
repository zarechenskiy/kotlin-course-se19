package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class ParserTest {

    @Before
    fun clear() {
        ExecutionContext.stdOut.clear()
    }

    @Test
    fun ifScenario() {
        var code = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        code = code.split(System.lineSeparator()).joinToString(separator = System.lineSeparator())

//        assertEquals(3, parser.file().block().statement().size)

        val lexer = LangLexer(CharStreams.fromString(code))
        val parser = LangParser(CommonTokenStream(lexer))

        val block = parser.file().block()
        assertNotNull(block)

        val statements = block.statement()
        assertEquals(3, statements.size)

        val varAStatement = statements[0]
        val varBStatement = statements[1]
        val ifStatement = statements[2]

        assertNotNull(varAStatement)
        assertNotNull(varBStatement)
        assertNotNull(ifStatement)

        val varAVariable = varAStatement.variable()
        assertNotNull(varAVariable)
        assertEquals("a", varAVariable.IDENTIFIER().text)
        assertEquals(10, varAVariable.expression().LITERAL().text.toInt())

        val varBVariable = varBStatement.variable()
        assertNotNull(varBVariable)
        assertEquals("b", varBVariable.IDENTIFIER().text)
        assertEquals(20, varBVariable.expression().LITERAL().text.toInt())

        val ifExpr = ifStatement.if_expr()
        assertNotNull(ifExpr)

        val condition = ifExpr.expression()
        assertNotNull(condition)

        val left = condition.left
        val right = condition.right

        assertNotNull(left)
        assertNotNull(right)

        assertEquals("a", left.text)
        assertEquals("b", right.text)

        val operator = condition.operator
        assertNotNull(operator)

        assertEquals(LangParser.GT, operator.type)
    }
}
