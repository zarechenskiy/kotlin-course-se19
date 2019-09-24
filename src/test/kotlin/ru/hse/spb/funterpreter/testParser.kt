package ru.hse.spb.funterpreter

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.hse.spb.parser.FunGrammarLexer
import ru.hse.spb.parser.FunGrammarParser

class TestParser {

    @Test
    fun parseLiteral() {
        val parser = parse(" \t 1234 \t \t\n")

        // NPE if something's wrong
        val statements = parser.file().block().statement()
        assertEquals(1, statements.size)
        assertEquals("1234", statements[0].expression().getToPoint().mult_divide().getLiteralText())
    }

    @Test
    fun parseNegativeLiteral() {
        val parser = parse(" \t -1234 \t \t\n")

        val statements = parser.file().block().statement()
        assertEquals(1, statements.size)
        assertEquals("-1234", statements[0].expression().getToPoint().mult_divide().getLiteralText())
    }

    @Test
    fun parsePlus() {
        val parser = parse("1 + 3 - 4")

        val statements = parser.file().block().statement()
        assertEquals(1, statements.size)
        val statement = statements[0]
        val minus = statement.expression().getToPoint()
        assertEquals("-", minus.ADD_OPERATOR().text)
        assertEquals("4", minus.mult_divide().getLiteralText())
        val plus = minus.add_subtract()
        assertEquals("3", plus.mult_divide().getLiteralText())
        assertEquals("+", plus.ADD_OPERATOR().text)
        assertEquals("1", plus.add_subtract().mult_divide().getLiteralText())
    }

    @Test
    fun parseIf() {
        val parser = parse("""
            if (1) {
                return 1
            } else {
                return 0
            }
        """.trimIndent())

        val statements = parser.file().block().statement()
        assertEquals(1, statements.size)
        val ifContext = statements[0].l_if()
        val blocks = ifContext.block_with_braces()
        assertEquals("1", blocks[0].block().statement()[0].l_return().expression().getToPoint().mult_divide().getLiteralText())
        assertEquals("0", blocks[1].block().statement()[0].l_return().expression().getToPoint().mult_divide().getLiteralText())
    }

    @Test
    fun parseComments() {
        val parser = parse("123 321 // comment #$%^&*\n 444 // comment")
        assertEquals(3, parser.file().block().statement().size)
    }

    private fun parse(program: String): FunGrammarParser {
        val lexer = FunGrammarLexer(CharStreams.fromString(program))
        return FunGrammarParser(CommonTokenStream(lexer))
    }
}

private fun FunGrammarParser.ExpressionContext.getToPoint() = binary_expression().or().and().equality().add_subtract()
private fun FunGrammarParser.Mult_divideContext.getLiteralText() = singular_expression().literal().LITERAL().text