package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class FplParseTreeTest {

    @Test
    fun parseTestVariable() {
        val program = "var x = 10"
        val result = FplTree(listOf(Variable("x", Optional.of(Literal(10)))))
        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseTestAssign() {
        val program = "x = 10"
        val result = FplTree(listOf(Assign("x", Literal(10))))
        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseTestIf() {
        val program = "if (x < 0) { return 1 }"
        val result = FplTree(listOf(If(Binary(Identifier("x"), Literal(0), BinaryOperation.LT),
                FplTree(listOf(Return(Literal(1)))),
                Optional.empty())))

        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseTestIfElse() {
        val program = "if (x < 0) { return 1 } else { return 0 }"
        val result = FplTree(listOf(If(Binary(Identifier("x"), Literal(0), BinaryOperation.LT),
                FplTree(listOf(Return(Literal(1)))),
                Optional.of(FplTree(listOf(Return(Literal(0))))))))

        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseWhile() {
        val program = "while (i < 0) { i = i + 1 }"
        val result = FplTree(listOf(
                While(Binary(Identifier("i"), Literal(0), BinaryOperation.LT),
                        FplTree(listOf(
                                Assign("i", Binary(Identifier("i"), Literal(1),
                                        BinaryOperation.PLUS))
                        )))
        ))
        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseReturn() {
        val program = "return 1"
        val result = FplTree(listOf(Return(Literal(1))))
        assertEquals(result, FplParser.parse(program))
    }

    @Test
    fun parseSimpleIf() {
        assertEquals(treeSimpleIf, FplParser.parse(programSimpleIf))
    }

    @Test
    fun parseInnerDef() {
        assertEquals(treeInnerDefTest, FplParser.parse(programInnerDefTest))
    }

    @Test
    fun parseFunAndStatements() {
        assertEquals(treeFunTest, FplParser.parse(programFunTest))
    }

    @Test
    fun parseComplicatedWithComment() {
        assertEquals(treeFibTest, FplParser.parse(programFibTest))
    }
}
