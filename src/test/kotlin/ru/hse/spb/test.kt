package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class Test {

    private fun run(name: String): Int? {
        val reader = CharStreams.fromFileName("src/test/kotlin/ru/hse/spb/tests/$name", Charsets.UTF_8)
        val parser = LangParser(CommonTokenStream(LangLexer(reader)))
        return evaluate(BlockVisitor().visit(parser.file()), State(null))
    }

    @Test
    fun testExpression() {
        assertEquals(-11, run("expressions"))
    }

    @Test
    fun testDeepExpression() {
        assertEquals(0, run("deepExpressions"))
    }

    @Test
    fun testIf() {
        assertEquals(0, run("if"))
    }

    @Test
    fun testFibonacci() {
        assertEquals(89, run("fibonacci"))
    }

    @Test
    fun testNestedFunctions() {
        assertEquals(42, run("nestedFunctions"))
    }
}