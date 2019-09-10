package ru.hse.spb

import java.util.*

/** Vertex in graph. Contains ids of it's neighbours. */
data class Vertex(
    val neighbourIds: HashSet<Int> = hashSetOf(),
    var used: Boolean = false)

/**
 * Fixed size undirected graph.
 */
class Graph(size: Int) {

    /** Vertices in the graph. */
    private val vertices = MutableList(size) { Vertex() }

    /** While searching for a cycle vertices in this list presents a path from the root. */
    private val pathIds = mutableListOf<Int>()

    /**
     * Get size of the graph.
     * It equals the number of vertices in the graph.
     */
    fun size(): Int {
        return vertices.size
    }

    /**
     * Check if vertex id is valid.
     * Should be 0 <= id < size.
     * Throws otherwise.
     */
    private fun checkVertexId(id: Int) {
        require (0 <= id && id < vertices.size) { "Vertex id is out of range." }
    }

    /**
     * Add a new edge between vertices id1 and id2 into the graph.
     * Throws if ids are invalid.
     */
    fun addEdge(id1: Int, id2: Int) {
        checkVertexId(id1)
        checkVertexId(id2)
        vertices[id1].neighbourIds.add(id2)
        vertices[id2].neighbourIds.add(id1)
    }

    /**
     * Remove an edge between vertices id1 and id2.
     * Throws if ids are invalid.
     */
    fun removeEdge(id1: Int, id2: Int) {
        checkVertexId(id1)
        checkVertexId(id2)
        vertices[id1].neighbourIds.remove(id2)
        vertices[id2].neighbourIds.remove(id1)
    }

    /**
     * Finds a cycle in graph and returns a list od vertices' ids in the cycle
     * in the order of their following in the graph.
     * The answer is empty if no cycle found.
     */
    fun findCycle(): List<Int> {
        if (vertices.isEmpty()) {
            return emptyList()
        }
        pathIds.clear()
        for (vertex in vertices) {
            vertex.used = false
        }

        return findCycle(vertexId = 0)
    }

    /** Recursive dfs function for finding a cycle. */
    private fun findCycle(vertexId: Int): List<Int> {
        if (vertices[vertexId].used) {
            return pathIds.drop(pathIds.indexOf(vertexId))
        }
        val parentId = pathIds.lastOrNull()
        pathIds.add(vertexId)
        vertices[vertexId].used = true

        val childrenIds = vertices[vertexId].neighbourIds
            .filter { it != parentId }
        for (childId in childrenIds) {
            val result = findCycle(vertexId = childId)
            if (result.isNotEmpty()) {
                return result
            }
        }
        pathIds.removeAt(pathIds.lastIndex)

        return emptyList()
    }

    /**
     * Calculate distances from vertexId vertex to all the reachable vertices.
     * Writes an answer into distance array.
     * It is presupposed that distance array is filled with -1 values.
     */
    fun calculateDistances(vertexId: Int, distance: IntArray) {
        val queue = mutableListOf<Int>()
        queue.add(vertexId)
        distance[vertexId] = 0

        while (queue.isNotEmpty()) {
            val id = queue.removeAt(0)
            for (neighbourId in vertices[id].neighbourIds) {
                if (distance[neighbourId] == -1) {
                    distance[neighbourId] = distance[id] + 1
                    queue.add(neighbourId)
                }
            }
        }
    }
}

/** Read info from input and create graph. */
fun createGraph(input: Scanner): Graph {
    val size = input.nextInt()
    val graph = Graph(size)

    repeat(size) {
        val id1 = input.nextInt() - 1
        val id2 = input.nextInt() - 1
        graph.addEdge(id1, id2)
    }

    return graph
}

/**
 * Find distances from the cycle for all the vertices in the graph.
 * It is presupposed that cycle exists.
 */
fun solveTask(input: Scanner): IntArray {
    val graph = createGraph(input)
    val distance = IntArray(graph.size()) { -1 }

    val cycleIds = graph.findCycle()

    for (i in cycleIds.indices) {
        if (i == 0) {
            graph.removeEdge(cycleIds[0], cycleIds.last())
        } else {
            graph.removeEdge(cycleIds[i], cycleIds[i - 1])
        }
    }

    for (id in cycleIds) {
        graph.calculateDistances(id, distance)
    }

    return distance
}


fun main() {
    val input = Scanner(System.`in`)
    val answer = solveTask(input)
    val answerString = answer.joinToString(" ")
    println(answerString)
}