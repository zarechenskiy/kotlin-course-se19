package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

// http://codeforces.com/contest/131/submission/60143264

class TestTaskSolver {

    private fun test(input: String, output: String) {
        val inScanner = Scanner(input)
        val answer = solveTask(inScanner)
        assertEquals(output, answer.joinToString(" "))
    }

    private fun testDistance(input: String, output: String, vertexId: Int = 0) {
        val inScanner = Scanner(input)
        val graph = createGraph(inScanner)
        val distance = IntArray(graph.size()) { -1 }

        graph.calculateDistances(vertexId, distance)

        assertEquals(output, distance.joinToString(" "))
    }

    @Test
    fun testSolveTask1() {
        test("4 1 3 4 3 4 2 1 2", "0 0 0 0")
    }

    @Test
    fun testSolveTask2() {
        test("6 1 2 3 4 6 4 2 3 1 3 3 5", "0 0 0 1 1 2")
    }

    @Test
    fun testSolveTask3() {
        test("3 1 2 1 3 2 3", "0 0 0")
    }

    @Test
    fun testSolveTask4() {
        test("4 3 1 3 4 2 1 4 2", "0 0 0 0")
    }


    @Test
    fun testSolveTask5() {
        test("4 4 3 1 3 2 1 2 3", "0 0 0 1")
    }

    @Test
    fun testCalculateDistance1() {
        testDistance("4 1 3 4 3 4 2 1 2", "0 1 1 2")
    }

    @Test
    fun testCalculateDistance2() {
        testDistance("6 1 2 3 4 6 4 2 3 1 3 3 5", "0 1 1 2 2 3")
    }

    @Test
    fun testCalculateDistance3() {
        testDistance("3 1 2 1 3 2 3", "0 1 1")
    }

    @Test
    fun testCalculateDistance4() {
        testDistance("4 3 1 3 4 2 1 4 2", "0 1 1 2")
    }


    @Test
    fun testCalculateDistance5() {
        testDistance("4 4 3 1 3 2 1 2 3", "0 1 1 2")
    }

    @Test
    fun testCalculateDistance6() {
        testDistance(
            "14 2 5 5 1 5 3 6 5 4 5 5 7 8 7 7 14 9 7 13 9 9 10 12 9 1 10 10 11",
            "1 3 3 3 2 3 2 3 1 0 1 2 2 3",
            9)
    }

}