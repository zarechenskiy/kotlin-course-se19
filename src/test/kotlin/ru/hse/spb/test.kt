package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.random.Random

class TestSource {
    @Test
    fun testTreap() {
        val treap = Treap()

        for (i in 0..10) {
            treap.put(i)
        }

        for (i in 0..10) {
            assertEquals(i, treap.get(i))
        }
    }

    @Test
    fun testSolutionSimple() {
        assertEquals("acb", solveProblem("bac", 2, listOf(Pair(2, 'a'), Pair(1, 'b'), Pair(2, 'c'))))
        assertEquals("baa", solveProblem("abacaba", 1, listOf(Pair(1, 'a'), Pair(1, 'a'), Pair(1, 'c'), Pair(2, 'b'))))
    }

    @Test
    fun testSolutionEmptyTrees() {
        assertEquals("ababababab", solveProblem("abf", 5, listOf(Pair(5, 'f'), Pair(4, 'f'), Pair(3, 'f'), Pair(2, 'f'), Pair(1, 'f'))))
        assertEquals("bbbbb", solveProblem("abf", 5, listOf(Pair(5, 'f'), Pair(4, 'f'), Pair(3, 'f'), Pair(2, 'f'), Pair(1, 'f'), Pair(1, 'a'), Pair(1, 'a'), Pair(1, 'a'), Pair(2, 'a'), Pair(1, 'a'))))
    }

    @Test
    fun testLongName() {
        val deleteList = LinkedList<Pair<Int, Char>>()

        for (i in 0 until 5 * 2000 - 1) {
            deleteList.add(Pair(1, 'a'))
        }

        assertEquals("a", solveProblem("aaaaa", 2000, deleteList))
    }
}