package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestSource {
    private fun checkInterpreter(program: String, expected: String) {
        val bos = ByteArrayOutputStream()
        val ps = PrintStream(bos)

        Interpreter.interpret(program, ps)
        assertEquals(expected, bos.toString().replace("\r\n", "\n").replace('\r', '\n'))
    }

    @Test
    fun testInterpreter() {
        val program1 = """
            |fun kek(a, b, c) {
            |   a = a + b + c
            |   println(a)
            |}
            |
            |var a = 10
            |var b = 20
            |if (a > b) {
            |    println(1)
            |} else {
            |   kek(228, a, b)
            |}
        """.trimMargin()

        val expected1 = """
            |258 
            |
        """.trimMargin()

        checkInterpreter(program1, expected1)

        val program2 = """
            |fun fib(n) {
            |   if (n <= 1) {
            |       return 1
            |   }
            |   
            |   return fib(n - 1) + fib(n - 2)
            |}
            |
            |var i = 1
            |while (i <= 5) {
            |   println(i, fib(i))
            |   i = i + 1
            |}
        """.trimMargin()

        val expected2 = """
            |1 1 
            |2 2 
            |3 3 
            |4 5 
            |5 8 
            |
        """.trimMargin()

        checkInterpreter(program2, expected2)

        val program3 = """
            |fun foo(n) {
            |   fun bar(m) {
            |       return m + n
            |   }
            |
            |   return bar(1)
            |}
            |
            | println(foo(41)) // prints 42
        """.trimMargin()

        val expected3 = """
            |42 
            |
        """.trimMargin()

        checkInterpreter(program3, expected3)
    }

    @Test
    fun testParser() {
        val program1 = """
            |fun kek(a, b, c) {
            |   a = a + b + c
            |   println(a)
            |}
            |
            |var a = 10
            |var b = 20
            |if (a > b) {
            |    println(1)
            |} else {
            |   kek(228, a, b)
            |}
        """.trimMargin()

        val lexer = FunLexer(CharStreams.fromString(program1))
        val parser = FunParser(CommonTokenStream(lexer))
        val root = parser.file().getChild(0)

        val kek = root.getChild(0).getChild(0)
        assertTrue(kek is FunParser.FunctionContext)
        assertEquals(3, (kek as FunParser.FunctionContext).parameters.size)
        assertEquals(2, kek.blockWithBraces().block().statement().size)

        val varA = root.getChild(1).getChild(0)

        assertTrue(varA is FunParser.VariableContext)
        assertEquals("a", (varA as FunParser.VariableContext).IDENTIFIER().toString())
        assertEquals("10", varA.expression().getChild(0).toString())

        val varB = root.getChild(3).getChild(0)
        assertTrue(varB is FunParser.VariableContext)
        assertEquals("b", (varB as FunParser.VariableContext).IDENTIFIER().toString())
        assertEquals("20", varB.expression().getChild(0).toString())

        val ifRule = root.getChild(5).getChild(0)
        assertTrue(ifRule is FunParser.IfRuleContext)
        assertEquals("a", (ifRule as FunParser.IfRuleContext).expression().getChild(0).getChild(0).toString())
        assertEquals(">", ifRule.expression().getChild(1).toString())
        assertEquals("b", ifRule.expression().getChild(2).getChild(0).toString())
    }
}