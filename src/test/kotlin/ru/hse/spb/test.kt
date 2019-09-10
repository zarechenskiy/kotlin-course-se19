package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TestSource {

    private fun listEquals(list1: List<Char>?, list2: List<Char>) : Boolean {
        if (list1 == null) {
            return false
        }

        if (list1.size != list2.size) {
            return false
        }

        for (i in 0 until list1.size) {
            if (list1[i] != list2[i]) {
                return false
            }
        }

        return true
    }

    @Test
    fun test1() {
        assertNull(solveProblem("a?c", 3))
    }

    @Test
    fun test2() {
        assertTrue(listEquals(solveProblem("a??a", 2), "abba".toList()))
    }

    @Test
    fun test3() {
        assertNull(solveProblem("a??a", 3))
    }

    @Test
    fun test4() {
        assertTrue(listEquals(solveProblem("?", 1), "a".toList()))
    }

    @Test
    fun test5() {
        assertTrue(listEquals(solveProblem("a??d?fgh???????beaabaih????c??", 9),
                "aacdafghiabaaebbeaabaihgfadcaa".toList()))
    }

    @Test
    fun test6() {
        assertTrue(listEquals(solveProblem( "??cdef??i??lmn?pq??tuv???z?u????b??no??????vhv?v?w?v?vh????????????n???u?z??wv?ts????nm???ih?f???b?",
                26), "abcdefahiaalmnapqastuvwaazauaaanbaanoaagjkrvhvxvywyvxvhvrkjgaaonaabnaaauazaawvutsaqpanmlaaihafedcba".toList()))
    }

    @Test
    fun test7() {
        assertNull(solveProblem("abcdeffcdfda", 6))
    }
}