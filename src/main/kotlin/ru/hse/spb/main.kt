package ru.hse.spb

private const val WRONG_INPUT_MESSAGE = "Wrong input"

/**
 * Reads stdin and solves Codeforces 59C problem (https://codeforces.com/problemset/problem/59/C)
 * using [ru.hse.spb.Solution]
 */
fun main() {
    val numberOfRequiredLetters = readLine()?.toIntOrNull()
    val pattern = readLine()

    if (numberOfRequiredLetters == null || pattern == null) {
        println(WRONG_INPUT_MESSAGE)
        return
    }

    println(Solution.solve(numberOfRequiredLetters, pattern))
}

/**
 * This class solves Codeforces 59C problem: https://codeforces.com/problemset/problem/59/C
 */
class Solution private constructor(numberOfRequiredLetters: Int,
                                           private val pattern: String) {

    private val requiredCharacterRange = FIRST_LETTER until FIRST_LETTER + numberOfRequiredLetters
    private val resultArray = Array(pattern.length) { ANY_LETTER }

    private fun prepareResultArray() {
        for (i in pattern.indices) {
            if (pattern[i] != ANY_LETTER) {
                resultArray[i] = pattern[i]
            } else {
                resultArray[i] = pattern[pattern.lastIndex - i]
            }
        }
    }

    private fun getLettersNotContainedInPattern() : HashSet<Char> {
        val notContainedLetters = requiredCharacterRange.toHashSet()
        notContainedLetters.removeAll(pattern.toList())
        return notContainedLetters
    }

    private fun resultArrayIsCorrect() : Boolean {
        for (i in 0..(resultArray.size / 2)) {
            if (resultArray[i] != resultArray[resultArray.lastIndex - i]) {
                return false
            }
            if (resultArray[i] !in requiredCharacterRange) {
                return false
            }
        }
        return true
    }

    private fun <E> MutableList<E>.popBack() : E? {
        if (size > 0) {
            val lastElement = last()
            removeAt(lastIndex)
            return lastElement
        }
        return null
    }

    private fun solveImpl() : String {
        prepareResultArray()

        val notContainedLetters = getLettersNotContainedInPattern().sorted().toMutableList()
        for (i in (resultArray.size - 1) / 2 downTo 0) {
            if (resultArray[i] != ANY_LETTER) {
                continue
            }
            val letterToWrite = notContainedLetters.popBack() ?: FIRST_LETTER
            resultArray[i] = letterToWrite
            resultArray[resultArray.lastIndex - i] = letterToWrite
        }

        if (notContainedLetters.isNotEmpty() || !resultArrayIsCorrect()) {
            return IMPOSSIBLE_MESSAGE
        }
        return resultArray.joinToString(separator = "")
    }

    companion object {
        /**
         * This string will be returned if solution does not exist
         */
        const val IMPOSSIBLE_MESSAGE = "IMPOSSIBLE"

        private const val ANY_LETTER = '?'
        private const val FIRST_LETTER = 'a'

        /**
         * Solves the problem and returns the answer
         */
        fun solve(numberOfRequiredLetters: Int, pattern: String) : String {
           return Solution(numberOfRequiredLetters, pattern).solveImpl()
        }
    }
}