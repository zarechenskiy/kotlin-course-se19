package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestSource {
    /** Trees from samples */
    private var tree1: Tree = Tree(3)
    private var tree2: Tree = Tree(4)
    private var tree3: Tree = Tree(5)

    @Before
    fun initializeTrees() {
        tree1 = Tree(3)
        tree1.addEdge(1, 2)
        tree1.addEdge(0, 1)

        tree2 = Tree(4)
        tree2.addEdge(1, 3)
        tree2.addEdge(1, 2)
        tree2.addEdge(1, 0)

        tree3 = Tree(5)
        tree3.addEdge(0, 1)
        tree3.addEdge(0, 2)
        tree3.addEdge(0, 3)
        tree3.addEdge(1, 4)
    }

    @Test
    fun findsCentroidCorrectly() {
        assertEquals(1, tree1.findCentroid())
        assertEquals(1, tree2.findCentroid())
        assertEquals(0, tree3.findCentroid())
    }

    @Test
    fun sortsSubtreesCorrectly() {
        assertTrue(mutableListOf(Edge(1, 2), Edge(1, 0)) ==
                        tree1.sortedSubtrees(1) ||
                        mutableListOf(Edge(1, 0), Edge(1, 2)) ==
                        tree1.sortedSubtrees(1))

        assertEquals(mutableListOf(Edge(1, 4), Edge(1, 0)),
                        tree3.sortedSubtrees(1))
    }

    @Test
    fun solvesProblemCorrectly() {
        paintAlmostAll(3, tree1.sortedSubtrees(1).iterator(), tree1)
        val edges = mutableListOf<Edge>()
        tree1.getSubtreeEdges(1, edges)
        assertTrue(checker(edges, 3))

        paintAlmostAll(4, tree2.sortedSubtrees(1).iterator(), tree2)
        edges.clear()
        tree2.getSubtreeEdges(1, edges)
        assertTrue(checker(edges, 4))

        paintAlmostAll(5, tree3.sortedSubtrees(0).iterator(), tree3)
        edges.clear()
        tree3.getSubtreeEdges(0, edges)
        assertTrue(checker(edges, 5))
    }

    private fun getAllDistances(vertexId: Int, edges: List<Edge>, have: MutableSet<Int>,
                                currentSum: Int = 0, parent: Int = -1) {
        have.add(currentSum)
        for (edge in edges) {
            if (edge.from == vertexId && edge.to != parent) {
                getAllDistances(edge.to, edges, have, currentSum + edge.weight,
                        edge.from)
            } else if (edge.to == vertexId && edge.from != parent) {
                getAllDistances(edge.from, edges, have, currentSum + edge.weight,
                        edge.to)
            }
        }
    }

    private fun checker(edges: List<Edge>, n: Int): Boolean {
        val values = mutableSetOf<Int>()
        for (i in 0 until n) {
            getAllDistances(i, edges, values)
        }
        for (i in 1..(2 * n * n / 9)) {
            if (!values.contains(i)) {
                return false
            }
        }
        return true
    }
}