package ru.hse.spb.interpreter

import junit.framework.Assert.assertEquals
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ru.hse.spb.parser.ProgrammeLexer
import ru.hse.spb.parser.ProgrammeParser

class ProgrammeInterpreterTest {

    fun execute(program: String): Int {
        val lexer = ProgrammeLexer(CharStreams.fromString(program))
        val parser = ProgrammeParser(CommonTokenStream(lexer))
        val interpreter = ProgrammeInterpreter(parser)
        return interpreter.run()
    }

    @Test
    fun returnInterpretationTest() {
        val program = """
            |var a = 2
            |var d = 3
            |
            |fun foo(b, c) {
            |   d = 4
            |   if (a < d) {
            |       return b + c
            |   }
            |   return 3
            |}
            |
            |return foo(3, 2)
        """.trimMargin()
        assertEquals(5, execute(program))
    }

    @Test
    fun testIfBlockTest() {
        val program = """
            |var a = 2
            |var d = 3
            |
            |if (a < d) {
            |   return a + d
            |}
            |
            |return 3
        """.trimMargin()
        assertEquals(5, execute(program))
    }

    @Test
    fun testDoubleIfBlockTest() {
        val program = """
            |var a = 2
            |var d = 3
            |
            |if (a < d) {
            |   if (a <= 1) {
            |       return 0
            |   } else {
            |       return 1
            |   }
            |}
            |
            |return 3
        """.trimMargin()
        //assertEquals(5, execute(program))
    }

    @Test
    fun functionInterpretationTest() {
        val program = """
            |var a = 2
            |fun foo(b, c) {
            |   a = b + c
            |}
            |
            |foo(3, 2)
            |return a
        """.trimMargin()
        execute(program)
    }

    @Test
    fun additionalExprTest() {
        assertEquals(13, execute("return 12 + 1"))
        assertEquals(1, execute("return 12 - 11"))
    }

    @Test
    fun multiplyExpTest() {
        assertEquals(33, execute("return 11 * 3"))
        assertEquals(3, execute("return 100 / 33"))
        assertEquals(2, execute("return 17 % 3"))
    }

    @Test
    fun equalityExpTest() {
        assertNotEquals(0, execute("return 5 != 3"))
        assertEquals(0, execute("return 5 == 3"))
    }

    @Test
    fun relationalExpTest() {
        assertEquals(0, execute("return 5 < 3"))
        assertNotEquals(0, execute("return 5 > 3"))

        assertEquals(0, execute("return 3 > 5"))
        assertNotEquals(0, execute("return 3 < 5"))

        assertEquals(0, execute("return 3 <= 2"))
        assertNotEquals(0, execute("return 2 <= 3"))
        assertNotEquals(0, execute("return 3 <= 3"))

        assertEquals(0, execute("return 2 >= 3"))
        assertNotEquals(0, execute("return 3 >= 2"))
        assertNotEquals(0, execute("return 3 >= 3"))
    }
}