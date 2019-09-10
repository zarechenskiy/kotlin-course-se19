package ru.hse.spb

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

internal class MainKtTest {

    @org.junit.jupiter.api.Test
    fun letterToIndexOfAIsZero() {
        assertEquals(0, letterToIndex('a'))
    }

    @org.junit.jupiter.api.Test
    fun letterToIndexOfZIsLetterInEnglish() {
        assertEquals(LETTERS_IN_ENGLISH - 1, letterToIndex('z'))
    }

    @org.junit.jupiter.api.Test
    fun indexToLetterOf0IsA() {
        assertEquals('a', indexToLetter(0))
    }

    @org.junit.jupiter.api.Test
    fun indexToLetterOf25IsZ() {
        assertEquals('z', indexToLetter(LETTERS_IN_ENGLISH - 1))
    }

    @org.junit.jupiter.api.Test
    fun SolveProblemInputTest1() {
        val inputData = ByteArrayOutputStream()

        inputData.use {
            val printer = PrintWriter(inputData)
            printer.use {
                printer.println(2)
                printer.println("bac")
                printer.println(3)
                printer.println("2 a")
                printer.println("1 b")
                printer.println("2 c")
            }
        }

        System.setIn(ByteArrayInputStream(inputData.toByteArray()))
        assertEquals("acb", solveProblem())
    }
}