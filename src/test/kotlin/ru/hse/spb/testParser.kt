package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser

class TestParser {
    private fun getTree(program: String): String {
        val expLexer = FunLexer(CharStreams.fromString(program))
        val parser = FunParser(BufferedTokenStream(expLexer))
        val file = parser.file()
        return file.toStringTree(parser)
    }

    @Test
    fun testNumber() {
        val tree = getTree("10")
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(numberLiteral 10))))))))))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testVariableDeclaration() {
        val tree = getTree("var a")
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(variable var
                    |(identifier a)))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testAssign() {
        val tree = getTree("var a = 10")
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(variable var
                    |(identifier a) =
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(numberLiteral 10)))))))))))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testArithmeticExpression() {
        val tree = getTree("a + b")
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(identifier a)))
                    |+
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(identifier b)))))))))))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testConditional() {
        val tree = getTree(
                """if (a > b) {
                    |} else {
                    |}
                    """.trimMargin())
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(ifCondition if (
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(identifier a))))
                    |>
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(identifier b)))))))))) )
                    |(blockWithBraces { block })
                    |else
                    |(blockWithBraces { block })))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testWhileLoop() {
        val tree = getTree(
                """ while (i < 10) {
                    |}
                    """.trimMargin())
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(whileLoop while (
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(identifier i))))
                    |<
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(numberLiteral 10)))))))))) )
                    |(blockWithBraces { block })))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testFunctionDefinition() {
        val tree = getTree(
                """ fun f(a, b) {
                    |   return 1
                    |}
                    """.trimMargin())
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(function fun
                    |(identifier f) (
                    |(identifier a) ,
                    |(identifier b) )
                    |(blockWithBraces {
                    |(block
                    |(statement
                    |(returnStatement return
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(numberLiteral 1))))))))))))
                    |})))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }

    @Test
    fun testFunctionCall() {
        val tree = getTree("f(1)")
        assertEquals(
                """(file
                    |(block
                    |(statement
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(functionCall
                    |(identifier f) (
                    |(expression
                    |(binaryExpression
                    |(orExpression
                    |(andExpression
                    |(compareExpression
                    |(summingExpression
                    |(multiplicatingExpression
                    |(singleExpression
                    |(numberLiteral 1)))))))))
                    |)))))))))))))"""
                        .trimMargin().replace('\n', ' '),
                tree)
    }
}