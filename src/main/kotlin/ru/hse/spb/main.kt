package ru.hse.spb

import java.io.FileReader
import java.util.*

const val LETTERS_IN_ENGLISH = 26
val p = Array(LETTERS_IN_ENGLISH) { mutableListOf<Int>() }

fun letterToInt(c : Char): Int {
    return c - 'a'
}

fun intToLetter(i : Int) : Char {
    return 'a' + i
}

fun main() {
    val input = Scanner(FileReader("input.txt"))

    input.use {
        val k = input.nextInt()
        input.nextLine()
        val s = input.nextLine()

        var currentPos = 0
        for (i in 1..k) {
            for (c in s) {
                p[letterToInt(c)].add(currentPos)
                currentPos++
            }
        }

        val n = input.nextInt()

        for (i in 1..n) {
            val pos = input.nextInt()
            val letter = input.next()[0]

            p[letterToInt(letter)].removeAt(pos - 1)
        }

        val resultName = CharArray(k * s.length)

        for (i in 0 until LETTERS_IN_ENGLISH) {
            for (j in p[i]) {
                resultName[j] = intToLetter(i)
            }
        }

        for (c in resultName) {
            if (c != Character.MIN_VALUE) {
                print(c)
            }
        }
    }
}