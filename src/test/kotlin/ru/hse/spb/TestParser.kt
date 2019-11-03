package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class TestParser {

    private fun parse(program: String): Block {
        val parser = LangParser(CommonTokenStream(LangLexer(CharStreams.fromString(program))))
        return BlockVisitor().visitFile(parser.file())
    }

    @Test
    fun testLiteral() {
        val program = "1"
        val tree = Block(listOf(Literal(1)))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testReturn() {
        val program = "return 1"
        val tree = Block(listOf(Return(Literal(1))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testVariable() {
        val program = "var x = 1"
        val tree = Block(listOf(Variable("x", Literal(1))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testAssignment() {
        val program = "x = 1"
        val tree = Block(listOf(Assignment("x", Literal(1))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testBinaryExpression() {
        val program = "1 + 2"
        val tree = Block(listOf(BinaryExpression(Literal(1), Literal(2), BinaryOperator.PLUS)))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testIf() {
        val program = "if (x > 0) {return 1} else {return 0}"
        val tree = Block(listOf(If(BinaryExpression(Identifier("x"), Literal(0), BinaryOperator.GT),
                Block(listOf(Return(Literal(1)))), Block(listOf(Return(Literal(0)))))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testWhile() {
        val program = "while (x > 0) {x = x - 1}"
        val tree = Block(listOf(While(BinaryExpression(Identifier("x"), Literal(0), BinaryOperator.GT),
                Block(listOf(Assignment("x", BinaryExpression(Identifier("x"), Literal(1), BinaryOperator.MINUS)))))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testFunction() {
        val program = "fun f(x, y) {return x}"
        val tree = Block(listOf(Function("f", listOf("x", "y"), Block(listOf(Return(Identifier("x")))))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testFunctionCall() {
        val program = "fun f(x, y) {return x} f(1, 2)"
        val tree = Block(listOf(Function("f", listOf("x", "y"), Block(listOf(Return(Identifier("x"))))),
                FunctionCall("f", listOf(Literal(1), Literal(2)))))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testDeepExpressions() {
        val program = "1 * 2 * 3"
        val tree = Block(listOf(BinaryExpression(BinaryExpression(Literal(1), Literal(2), BinaryOperator.MULT), Literal(3), BinaryOperator.MULT)))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testStupidExpressionsWithBrackets() {
        val program = "(1)"
        val tree = Block(listOf(Literal(1)))
        assertEquals(tree, parse(program))
    }

    @Test
    fun testExpressionsWithBrackets() {
        val program = "(1 * 2)"
        val tree = Block(listOf(BinaryExpression(Literal(1), Literal(2), BinaryOperator.MULT)))
        assertEquals(tree, parse(program))
    }
}