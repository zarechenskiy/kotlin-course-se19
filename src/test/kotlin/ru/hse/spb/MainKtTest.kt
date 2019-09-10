package ru.hse.spb

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

internal class MainKtTest {

    @Test
    fun letterToIndexOfAIsZero() {
        assertEquals(0, letterToIndex('a'))
    }

    @Test
    fun letterToIndexOfZIsLetterInEnglish() {
        assertEquals(LETTERS_IN_ENGLISH - 1, letterToIndex('z'))
    }

    @Test
    fun indexToLetterOf0IsA() {
        assertEquals('a', indexToLetter(0))
    }

    @Test
    fun indexToLetterOf25IsZ() {
        assertEquals('z', indexToLetter(LETTERS_IN_ENGLISH - 1))
    }

    @Test
    fun solveProblemInputTest1() {
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

    @Test
    fun solveProblemInputTest2() {
        val inputData = ByteArrayOutputStream()

        inputData.use {
            val printer = PrintWriter(inputData)
            printer.use {
                printer.println(1)
                printer.println("abacaba")
                printer.println(4)
                printer.println("1 a")
                printer.println("1 a")
                printer.println("1 c")
                printer.println("2 b")
            }
        }

        System.setIn(ByteArrayInputStream(inputData.toByteArray()))
        assertEquals("baa", solveProblem())
    }

    @Test
    fun solveProblemInputTest3() {
        val inputData = ByteArrayOutputStream()

        inputData.use {
            val printer = PrintWriter(inputData)
            printer.use {
                printer.println(1)
                printer.println("aa")
                printer.println(1)
                printer.println("1 a")
            }
        }

        System.setIn(ByteArrayInputStream(inputData.toByteArray()))
        assertEquals("a", solveProblem())
    }

    @Test
    fun solveProblemInputTest4() {
        val inputData = ByteArrayOutputStream()

        inputData.use {
            val printer = PrintWriter(inputData)
            printer.use {
                printer.println(1)
                printer.println("ab")
                printer.println(1)
                printer.println("1 b")
            }
        }

        System.setIn(ByteArrayInputStream(inputData.toByteArray()))
        assertEquals("a", solveProblem())
    }
}