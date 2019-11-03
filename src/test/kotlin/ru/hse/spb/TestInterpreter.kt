package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class TestInterpreter {

    @Test
    fun testExpressionClasses() {
        val literal: Expression = Literal(1)
        assert(literal is Literal && literal.literal == 1)

        val identifier: Expression = Identifier("a")
        assert(identifier is Identifier && identifier.identifier == "a")

        val binaryExpression: Expression = BinaryExpression(Literal(1), Literal(2), BinaryOperator.PLUS)
        assert(binaryExpression is BinaryExpression && binaryExpression.operator.apply(1, 2) == 3)
    }

    @Test
    fun testEvaluateExpressions() {
        val literal: Expression = Literal(1)
        assertEquals(1, evaluate(literal, State(null)))

        val identifier: Expression = Identifier("a")
        val state = State(null)
        state.registerVariable("a", 1)
        assertEquals(1, evaluate(identifier, state))

        val binaryExpression: Expression = BinaryExpression(Literal(1), Literal(2), BinaryOperator.PLUS)
        assertEquals(3, evaluate(binaryExpression, state))
    }

    /*
    s = 0, i = 10
    while i > 0
        s = s + i
        i = i - 1
     */
    @Test
    fun testWhile() {
        val state = State(null)
        state.registerVariable("s", 0)
        state.registerVariable("i", 10)
        val statement: Statement = While(
                BinaryExpression(Identifier("i"), Literal(0), BinaryOperator.GT),
                Block(listOf(Assignment("s", BinaryExpression(Identifier("s"), Identifier("i"), BinaryOperator.PLUS)),
                        Assignment("i", BinaryExpression(Identifier("i"), Literal(1), BinaryOperator.MINUS))))
        )
        evaluate(statement, state)
        assertEquals(55, state.getVariable("s"))
    }

    /*
    fun f(x) = x

    return f(1)
     */
    @Test
    fun testSimpleFunction() {
        val state = State(null)
        state.registerFunction("f", Function("f", listOf("x"), Block(
                listOf(Return(Identifier("x")))
        )))
        val statement = FunctionCall("f", listOf(Literal(1)))
        assertEquals(1, evaluate(statement, state))
    }

    /*
    fun f(x) =
        if (x == 0) 1
        else x * f(x - 1)

     return f(5)
     */
    @Test
    fun testRecursion() {
        val state = State(null)
        state.registerFunction("f", Function("f", listOf("x"), Block(
                listOf(If(BinaryExpression(Identifier("x"), Literal(0), BinaryOperator.EQ),
                        Block(listOf(Return(Literal(1)))),
                        Block(listOf(Return(BinaryExpression(Identifier("x"),
                                FunctionCall("f", listOf(BinaryExpression(Identifier("x"), Literal(1), BinaryOperator.MINUS))),
                                BinaryOperator.MULT)))))))))
        val statement = FunctionCall("f", listOf(Literal(5)))
        assertEquals(120, evaluate(statement, state))
    }

    @Test(expected = RuntimeException::class)
    fun testUnknownVariable() {
        val state = State(null)
        state.getVariable("x")
    }

    @Test(expected = RuntimeException::class)
    fun testUnknownFunction() {
        val state = State(null)
        state.getFunction("x")
    }

    @Test(expected = RuntimeException::class)
    fun testMultipleDefinition() {
        val state = State(null)
        state.registerVariable("x", 0)
        state.registerVariable("x", 1)
    }

    @Test
    fun testPrintln() {
        val state = State(null)
        val statement = FunctionCall("println", listOf(Literal(1), Literal(2)))
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
        evaluate(statement, state)
        assertEquals("1 2\n", outContent.toString())
        System.setOut(System.out)
    }
}