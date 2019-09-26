package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class FplTest {
    private fun run(program: String): Int =
            FplInterpreter(PrintStream(ByteArrayOutputStream())).interpret(FplParser.parse(program))

    @Test
    fun testSimpleIf() {
        assertEquals(30, run(programSimpleIf))
    }

    @Test
    fun testFunTest() {
        assertEquals(6, run(programFunTest))
    }

    @Test
    fun testInnerDefTest() {
        assertEquals(11, run(programInnerDefTest))
    }

    @Test
    fun testFibTest() {
        assertEquals(13, run(programFibTest))
    }

}