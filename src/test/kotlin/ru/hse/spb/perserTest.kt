package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.hse.spb.parser.FunLanguageLexer
import ru.hse.spb.parser.FunLanguageParser

class ParserTest {
    @Test
    fun testBasic() {
        val parser = getParser(basicCode)

        val statements = parser.file().block().statement()
        assertEquals(5, statements.size)

        assertNotNull(statements[0].variable())
        assertNull(statements[0].expression())

        assertNotNull(statements[1].variable())
        assertNotNull(statements[1].variable().expression().MULTIPLICATION())

        assertNotNull(statements[2].function())
        assertEquals(1, statements[2].function().blockWithBraces().block().statement().size)
        assertNotNull(statements[2].function().blockWithBraces().block().statement(0).returnStatement())

        assertNotNull(statements[3].ifStatement())
        assertEquals(2, statements[3].ifStatement().blockWithBraces().size)

        assertNotNull(statements[4].whileStatement())
        assertEquals("1", statements[4].whileStatement().expression().simpleExpression().LITERAL().text)
    }

    @Test
    fun testBinaryOperatorsPrecedence() {
        val parser = getParser(complexBinaryExpressionCode)

        var currentExpression = parser.file().block().statement(0).expression()
        assertNotNull(currentExpression.MULTIPLICATION())
        assertEquals("1", currentExpression.expression(0).simpleExpression().LITERAL().text)

        currentExpression = currentExpression.expression(1).simpleExpression().expression()
        assertNotNull(currentExpression.LOGIC_OR())

        currentExpression = currentExpression.expression(0)
        assertNotNull(currentExpression.GTE())

        currentExpression = currentExpression.expression(0)
        assertNotNull(currentExpression.ADDITION())

        currentExpression = currentExpression.expression(1)
        assertNotNull(currentExpression.MULTIPLICATION())
    }

    @Test
    fun testNestedCode() {
        val parser = getParser(nestedCode)

        assertEquals(
                "13",
                parser.file().block()
                        .statement(0).function().blockWithBraces().block()
                        .statement(0).function().blockWithBraces().block()
                        .statement(0).whileStatement().blockWithBraces().block()
                        .statement(0).ifStatement().blockWithBraces(1).block()
                        .statement(0).whileStatement().blockWithBraces().block()
                        .statement(0).function().blockWithBraces().block()
                        .statement(0).assignment().expression().simpleExpression().LITERAL().text
        )
    }

    private fun getParser(code: String): FunLanguageParser {
        val lexer = FunLanguageLexer(CharStreams.fromString(code))
        return FunLanguageParser(CommonTokenStream(lexer))
    }

    private companion object {
        private val basicCode = """
            |var a
            |var b = 3 * 3
            |
            |fun function() {
            |    return 1
            |}
            |
            |if (function()) {
            |
            |} else {
            |
            |}
            |
            |while(1) {
            |
            |}
        """.trimMargin()

        private val complexBinaryExpressionCode = """
            | 1 * (2 + 2 * 2 >= 8 || 3)
        """.trimMargin()

        private val nestedCode = """
            |fun function1() {
            |   fun function2() {
            |       while(i == 0) {
            |           if (2 == 3) {
            |           } else {
            |               while (1 == 1) {
            |                   fun function3() {
            |                       a = 13
            |                   }
            |               }
            |           }
            |       }
            |   }
            |}
            |
        """.trimMargin()
    }
}