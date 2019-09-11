package ru.hse.spb

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class TestSource {

    /*
     * Graph descriptions.
     *
     * Cycle graph on 4 vertices (the test from the task's example).
     */
    private fun getGraphCycle1() : Graph {
        val graph = Graph(4)
        graph.connectVertices(0, 1)
        graph.connectVertices(1, 3)
        graph.connectVertices(3, 2)
        graph.connectVertices(2, 0)

        return graph
    }

    /*
     * Right result for the `findCycle` call on the graph above.
     */
    private fun getGraphCycle1FCResult() : BooleanArray {
       return booleanArrayOf(true, true, true, true)
    }

    /*
     * Right result for the `findDistances` call on the graph above.
     */
    private fun getGraphCycle1DistResult() : IntArray {
        return intArrayOf(0, 0, 0, 0)
    }

    /*
     * Cycle graph on 3 vertices (the smallest possible graph).
     */
    private fun getGraphCycle2() : Graph {
        val graph = Graph(3)
        graph.connectVertices(0, 1)
        graph.connectVertices(1, 2)
        graph.connectVertices(2, 0)

        return graph
    }

    private fun getGraphCycle2FCResult() : BooleanArray {
        return booleanArrayOf(true, true, true)
    }

    private fun getGraphCycle2DistResult() : IntArray {
        return intArrayOf(0, 0, 0)
    }

    /*
     * Graph on 6 vertices. Cycle [0, 1, 2].
     */
    private fun getGraphTest1() : Graph {
        val graph = Graph(6)
        graph.connectVertices(0, 1)
        graph.connectVertices(0, 2)
        graph.connectVertices(1, 2)
        graph.connectVertices(2, 4)
        graph.connectVertices(2, 3)
        graph.connectVertices(3, 5)

        return graph
    }

    private fun getGraphTest1FCResult() : BooleanArray {
        return booleanArrayOf(true, true, true, false, false, false)
    }

    private fun getGraphTest1DistResult() : IntArray {
        return intArrayOf(0, 0, 0, 1, 1, 2)
    }

    /*
     * Graph on 7 vertices. Cycle [1, 4, 5].
     */
    private fun getGraphTest2() : Graph {
        val graph = Graph(7)
        graph.connectVertices(0, 3)
        graph.connectVertices(5, 3)
        graph.connectVertices(5, 1)
        graph.connectVertices(4, 1)
        graph.connectVertices(4, 5)
        graph.connectVertices(2, 4)
        graph.connectVertices(2, 6)

        return graph
    }

    private fun getGraphTest2FCResult() : BooleanArray {
        return booleanArrayOf(false, true, false, false, true, true, false)
    }

    private fun getGraphTest2DistResult() : IntArray {
        return intArrayOf(2, 0, 1, 1, 0, 0, 2)
    }

    /*
     * Graph on 8 vertices. Cycle [2, 5, 4, 7].
     */
    private fun getGraphTest3() : Graph {
        val graph = Graph(8)
        graph.connectVertices(0, 5)
        graph.connectVertices(6, 2)
        graph.connectVertices(1, 7)
        graph.connectVertices(4, 3)
        graph.connectVertices(4, 7)
        graph.connectVertices(4, 5)
        graph.connectVertices(5, 2)
        graph.connectVertices(7, 2)
        return graph
    }

    private fun getGraphTest3FCResult() : BooleanArray {
        return booleanArrayOf(false, false, true, false, true, true, false, true)
    }

    private fun getGraphTest3DistResult() : IntArray {
        return intArrayOf(1, 1, 0, 1, 0, 0, 1, 0)
    }

    /*
     * Graph on 8 vertices. Cycle [0, 1, 2].
     */
    private fun getGraphTest4() : Graph {
        val graph = Graph(8)
        graph.connectVertices(0, 1)
        graph.connectVertices(1, 2)
        graph.connectVertices(2, 0)
        graph.connectVertices(3, 2)
        graph.connectVertices(3, 4)
        graph.connectVertices(5, 4)
        graph.connectVertices(5, 6)
        graph.connectVertices(5, 7)
        return graph
    }

    private fun getGraphTest4FCResult() : BooleanArray {
        return booleanArrayOf(true, true, true, false, false, false, false, false)
    }

    private fun getGraphTest4DistResult() : IntArray {
        return intArrayOf(0, 0, 0, 1, 2, 3, 4, 4)
    }


    /*
     * Generic test for the `findCycle` call.
     */
    private fun findCycleTest(graph: Graph, result: BooleanArray) {
       assertArrayEquals(result, findCycle(graph).toBooleanArray())
    }

    /*
     * Generic test for the `findDistances` call.
     */
    private fun findDistanceTest(graph: Graph, result: IntArray) {
        assertArrayEquals(result, findDistances(graph).toIntArray())
    }

    @Test
    fun testFCTest1() {
       findCycleTest(getGraphCycle1(), getGraphCycle1FCResult())
    }

    @Test
    fun testFCTest2() {
        findCycleTest(getGraphCycle2(), getGraphCycle2FCResult())
    }

    @Test
    fun testFCTest3() {
        findCycleTest(getGraphTest1(), getGraphTest1FCResult())
    }

    @Test
    fun testFCTest4() {
        findCycleTest(getGraphTest2(), getGraphTest2FCResult())
    }

    @Test
    fun testFCTest5() {
        findCycleTest(getGraphTest3(), getGraphTest3FCResult())
    }

    @Test
    fun testFCTest6() {
        findCycleTest(getGraphTest4(), getGraphTest4FCResult())
    }

    @Test
    fun testDistTest1() {
        findDistanceTest(getGraphCycle1(), getGraphCycle1DistResult())
    }

    @Test
    fun testDistTest2() {
        findDistanceTest(getGraphCycle2(), getGraphCycle2DistResult())
    }

    @Test
    fun testDistTest3() {
        findDistanceTest(getGraphTest1(), getGraphTest1DistResult())
    }

    @Test
    fun testDistTest4() {
        findDistanceTest(getGraphTest2(), getGraphTest2DistResult())
    }

    @Test
    fun testDistTest5() {
        findDistanceTest(getGraphTest3(), getGraphTest3DistResult())
    }

    @Test
    fun testDistTest6() {
        findDistanceTest(getGraphTest4(), getGraphTest4DistResult())
    }

}