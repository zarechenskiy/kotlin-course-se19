package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import ru.hse.spb.parser.FunCallParser.*
import java.io.PrintStream
import java.io.ByteArrayOutputStream


class TestSource {
    private val program= """
            |fun min(x,y) {
            |   if (x<y) {
            |       return x
            |   } else {
            |       return y
            |   }
            |}
            |var a = 2
            |a = 7*3-13
            |println(min(a, 4), a)
        """.trimMargin()

    @Test
    fun testParser() {
        val lexer = FunCallLexer(CharStreams.fromString(program))
        val parser = FunCallParser(BufferedTokenStream(lexer))
        val ast = parser.file().block()

        val func = ast.statement(0).func()
        assertTrue(func is FuncContext)
        assertTrue(func.params() is ParamsContext)
        assertEquals("min", func.IDENTIFIER().text)
        assertEquals("x", func.params().IDENTIFIER(0).text)
        assertEquals("y", func.params().IDENTIFIER(1).text)
        assertTrue(func.brBlock().block() is BlockContext)

        val funcIf = func.brBlock().block().statement(0).ifSt()

        assertTrue(funcIf is IfStContext)
        assertTrue(funcIf.getChild(3) is OrdExprContext)
        assertTrue(funcIf.brBlock(0).block().statement(0).returnSt() is ReturnStContext)
        assertTrue(funcIf.brBlock(1).block().statement(0).returnSt() is ReturnStContext)

        val variable = ast.statement(1).`var`()
        assertTrue(variable is VarContext)
        assertEquals("a", variable.IDENTIFIER().text)
        assertEquals(2, variable.expr().text.toInt())

        val assign = ast.statement(2).assign()
        assertTrue(assign is AssignContext)
        assertEquals("a", assign.IDENTIFIER().text)
        assertEquals("7*3-13", assign.expr().text)
        assertTrue(assign.getChild(4) is SumExprContext)

        val print = ast.statement(3).expr()
        val printArg1 = print.getChild(2).getChild(0)
        val printArg2 = print.getChild(2).getChild(3)
        assertTrue(print is PrintExprContext)
        assertTrue(printArg1 is CallExprContext)
        assertEquals("a", printArg1.getChild(2).getChild(0).text)
        assertEquals(4, printArg1.getChild(2).getChild(3).text.toInt())
        assertEquals("a", printArg2.text)
    }

    @Test
    fun testInterpreter() {
        fun interpret(program: String): String {
            val originalOut = System.out
            val outContent = ByteArrayOutputStream()
            System.setOut(PrintStream(outContent))

            val lexer = FunCallLexer(CharStreams.fromString(program))
            val parser = FunCallParser(BufferedTokenStream(lexer))
            parser.file().accept(StatementsEvaluationVisitor())

            System.setOut(originalOut)
            return outContent.toString()
        }

        val programA = """
            var a = 10
            var b = 20
            if (a>b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        val programB = """
            fun fib(n) {
                if (n<=1) {
                    return 1
                } else {
                    return fib(n-1)+fib(n-2)
                }
            }
            
            var i = 1
            while (i<=5) {
                println(i, fib(i))
                i = i+1
            }
        """.trimIndent()


        val programC = """
            fun foo(n) {
            fun bar(m) {
                return m+n
            }
        
                return bar(1)
            }
            
            println(foo(41)) // prints 42
        """.trimIndent()

        assertEquals("4\n8\n", interpret(program))
        assertEquals("0\n", interpret(programA))
        assertEquals("1\n1\n2\n2\n3\n3\n4\n5\n5\n8\n", interpret(programB))
        assertEquals("42\n", interpret(programC))
    }
}
