package ru.hse.spb

import java.util.*

const val LETTERS_IN_ENGLISH = 26

/**
 * Converts english letters to it's indexes in the english alphabet (from 0) without
 * boundary checks
 */
fun letterToIndex(c : Char): Int {
    return c - 'a'
}

/**
 * Converts an index in the english alphabet to the corresponding english letter
 * without boundary checks
 */
fun indexToLetter(i : Int) : Char {
    return 'a' + i
}

fun solveProblem() : String {
    val result = StringBuilder()

    val input = Scanner(System.`in`)

    input.use {
        val k = input.nextInt()
        input.nextLine()
        val s = input.nextLine()

        val lettersPositions = Array(LETTERS_IN_ENGLISH) { mutableListOf<Int>() }

        var currentPosition = 0
        for (i in 1..k) {
            for (c in s) {
                lettersPositions[letterToIndex(c)].add(currentPosition)
                currentPosition++
            }
        }

        val n = input.nextInt()

        for (i in 1..n) {
            val pos = input.nextInt()
            val letter = input.next()[0]

            lettersPositions[letterToIndex(letter)].removeAt(pos - 1)
        }

        val resultName = CharArray(k * s.length)

        for (i in 0 until LETTERS_IN_ENGLISH) {
            for (j in lettersPositions[i]) {
                resultName[j] = indexToLetter(i)
            }
        }

        for (c in resultName) {
            if (c != Character.MIN_VALUE) {
                result.append(c)
            }
        }
    }

    return result.toString()
}

fun main() {
    print(solveProblem())
}