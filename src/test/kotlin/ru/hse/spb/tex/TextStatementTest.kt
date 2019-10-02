package ru.hse.spb.tex

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class TextStatementTest {
    @Test
    fun stringRepresentationTest() {
        val string = "qweqwe"
        assertEquals(string + "\n", TextStatement(string).stringRepresentation)
    }

    @Test
    fun `repeated stringRepresentation test`() {
        val string = "qweqwe"
        val statement = TextStatement(string)
        assertEquals(string + "\n", statement.stringRepresentation)
        assertEquals(string + "\n", statement.stringRepresentation)
    }
}