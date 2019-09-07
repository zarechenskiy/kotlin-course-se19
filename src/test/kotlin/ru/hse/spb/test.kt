package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testFromCodeforces1() {
        assertEquals(
                Solution.IMPOSSIBLE_MESSAGE,
                Solution.solve(3, "a?c")
        )
    }

    @Test
    fun testFromCodeforces2() {
        assertEquals(
                "abba",
                Solution.solve(2, "a??a")
        )
    }

    @Test
    fun testFromCodeforces3() {
        assertEquals(
                "abba",
                Solution.solve(2, "?b?a")
        )
    }

    @Test
    fun testPatternContainsWrongLetter() {
        assertEquals(
                Solution.IMPOSSIBLE_MESSAGE,
                Solution.solve(2, "abcba")
        )
    }

    @Test
    fun testPatternCouldNotBeTurnedIntoAPalindrome() {
        assertEquals(
                Solution.IMPOSSIBLE_MESSAGE,
                Solution.solve(2, "ababba")
        )
    }

    @Test
    fun testAnswerCouldNotContainAllRequiredLetters() {
        assertEquals(
                Solution.IMPOSSIBLE_MESSAGE,
                Solution.solve(4, "??a??")
        )
    }

    @Test
    fun testComplementsPatternRight() {
        assertEquals(
                "dacbbcad",
                Solution.solve(4, "dacb????")
        )
        assertEquals(
                "dacbbcad",
                Solution.solve(4, "????bcad")
        )
        assertEquals(
                "dacbbcad",
                Solution.solve(4, "d?c?b?a?")
        )
    }

    @Test
    fun testPutsAllRequiredLettersInRightOrder() {
        assertEquals(
                "aaaaabcbaaaaa",
                Solution.solve(3,"?????????????")
        )
    }
}