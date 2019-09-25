package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import java.io.PrintStream
import java.lang.IllegalArgumentException

class TestSource {
    private fun execute(program: String): Int {
        val lexer = FunCallLexer(CharStreams.fromString(program))
        val parser = FunCallParser(CommonTokenStream(lexer))
        val visitor = Visitor()
        parser.file().accept(visitor)
        return visitor.stack.last()
    }

    @Test
    fun oneReturnTest() {
        assertEquals(1, execute("return 1"))
    }

    @Test
    fun oneNegativeNumberReturnTest() {
        assertEquals(-1, execute("return -1"))
    }

    @Test
    fun addOperationReturnTest() {
        assertEquals(2, execute("return 1 + 1"))
    }

    @Test
    fun divOperationReturnTest() {
        assertEquals(1, execute("return 1 / 1"))
    }

    @Test
    fun subOperationReturnTest() {
        assertEquals(0, execute("return 1 - 1"))
    }

    @Test
    fun eqOperationReturnTest() {
        assertEquals(1, execute("return 1 == 1"))
        assertEquals(0, execute("return 10 == 1"))
    }

    @Test
    fun neqOperationReturnTest() {
        assertEquals(1, execute("return 10 != 1"))
        assertEquals(0, execute("return 1 != 1"))
    }

    @Test
    fun leOperationReturnTest() {
        assertEquals(1, execute("return 1 <= 1"))
        assertEquals(1, execute("return -1 <= 1"))
        assertEquals(0, execute("return 10 <= 1"))
    }

    @Test
    fun lOperationReturnTest() {
        assertEquals(0, execute("return 1 < 1"))
        assertEquals(1, execute("return -1 < 1"))
        assertEquals(0, execute("return 10 < 1"))
    }

    @Test
    fun geOperationReturnTest() {
        assertEquals(1, execute("return (1 >= 1)"))
        assertEquals(0, execute("return (-1 >= 1)"))
        assertEquals(1, execute("return (10 >= 1)"))
    }

    @Test
    fun gOperationReturnTest() {
        assertEquals(0, execute("return 1 > 1"))
        assertEquals(0, execute("return -1 > 1"))
        assertEquals(1, execute("return 10 > 1"))
    }

    @Test
    fun binOperationWithBrackets() {
        assertEquals(5, execute("return (10 > 1) * (10 / 2)"))
    }

    @Test
    fun returnZeroTest() {
        assertEquals(0, execute(""))
    }

    @Test
    fun returnVarTest() {
        assertEquals(1, execute("""var a = 1
            |return a
        """.trimMargin()))
    }

    @Test
    fun severalVariablesOperationsTest() {
        assertEquals(10, execute(
                """var a = 1
                |var b = 9
                |var _c = a + b
                |return _c
        """.trimMargin()))
    }

    @Test
    fun falseIfTest() {
        assertEquals(0, execute(
                """if (1 > 1) {
                    return 10
                    }
        """.trimMargin()))
    }

    @Test
    fun trueIfTest() {
        assertEquals(10, execute(
                """if (1 == 1) {
                    return 10
                    }
        """.trimMargin()))
    }

    @Test
    fun trueIfWithElseTest() {
        assertEquals(10, execute(
                """if (1 == 1) {
                    return 10
                    } else {
                    return 5
                    }
        """.trimMargin()))
    }

    @Test
    fun falseIfWithElseTest() {
        assertEquals(1, execute(
                """if (1 > 1) {
                    return 10
                    } else {
                        return 1
                    }
        """.trimMargin()))
    }

    @Test
    fun whileTest() {
        assertEquals(2, execute(
                """var a = 0
                    |while (a < 2) {
                    |   a = a + 1
                    |}
                    |return a
        """.trimMargin()))
    }


    @Test
    fun emptyFunctionCallTest() {
        assertEquals(2, execute(
                """fun kek() {
                    |return 2
                    |}
                    |return kek()
        """.trimMargin()))
    }

    @Test
    fun oneParameterfunctionCall() {
        assertEquals(2, execute(
                """fun kek(a) {
                    |return a
                    |}
                    |return kek(2)
        """.trimMargin()))
    }

    @Test
    fun varParameterfunctionCall() {
        assertEquals(6, execute(
                """fun kek(a, b) {
                    |return a * b
                    |}
                    |var c = 3
                    |return kek(2, c)
        """.trimMargin()))
    }

    @Test
    fun unknownVariableTest() {
        assertThrows<IllegalArgumentException> {
            execute(
                    """ fun kek(){
                    |var a = 0
                    |}
                    |
                    |return a
        """.trimMargin())
        }
    }

    @Test
    fun uninitializedVariableTest() {
        assertThrows<IllegalArgumentException> {
            execute(
                    """ var a
                    |return a
        """.trimMargin())
        }
    }

    @Test
    fun doubleFunctionDefitionTest() {
        assertThrows<IllegalArgumentException> {
            execute(
                    """ fun a() {
                        |}
                        |fun a(b) {
                        |}
        """.trimMargin())
        }
    }

    @Test
    fun doubleVariableDefinitionTest() {
        assertThrows<IllegalArgumentException> {
            execute(
                    """ var a = 1
                        |var a = 2
                    |return a
        """.trimMargin())
        }
    }
}