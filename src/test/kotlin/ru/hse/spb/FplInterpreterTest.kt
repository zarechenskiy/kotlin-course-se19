package ru.hse.spb

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

class FplInterpreterTest {
    @Test
    fun testRunSimple() {
        // var x = 5 * 3
        // return x + 2
        val tree = FplTree(listOf(
                Variable("x", Optional.of(Binary(Literal(5), Literal(3), BinaryOperation.MULT))),
                Return(Binary(Identifier("x"), Literal(2), BinaryOperation.PLUS))
        ))
        assertEquals(17, FplInterpreter().interpret(tree))
    }

    @Test
    fun testIf() {
        assertEquals(30, FplInterpreter().interpret(treeSimpleIf))
    }

    @Test
    fun testIfOtherCondition() {
        assertEquals(5, FplInterpreter().interpret(treeSimpleIfOtherCondition))
    }

    @Test
    fun testSimpleFun() {
        assertEquals(6, FplInterpreter().interpret(treeFunTest))
    }

    @Test
    fun testInnerDef() {
        assertEquals(11, FplInterpreter().interpret(treeInnerDefTest))
    }

    @Test
    fun testComplicatedFibonacci() {
        val result = "1, 1\n2, 2\n3, 3\n4, 5\n5, 8\n".toByteArray()
        val out = ByteArrayOutputStream()
        val int = FplInterpreter(PrintStream(out))

        val res = int.interpret(treeFibTest)
        assertEquals(13, res)

        val ba = out.toByteArray()
        assertArrayEquals(ba, result)
    }
}