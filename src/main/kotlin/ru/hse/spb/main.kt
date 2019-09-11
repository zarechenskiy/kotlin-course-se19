package ru.hse.spb

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Undirected graph on `size` vertices.
 */
class Graph(private val _size: Int) {
    private val graph = MutableList(_size) { mutableListOf<Int>() }

    val size: Int
        get() = _size

    /**
     * Connect two vertices with undirected edge.
     *
     * It's supposed that the vertex index is less than `size`.
     */
    fun connectVertices(to: Int, from: Int) {
        graph[to].add(from)
        graph[from].add(to)
    }

    /**
     * Return immutable list of neighbours of the given node.
     */
    fun neighbours(node: Int): List<Int> = graph[node]
}

/**
 *  Find a cycle in the connected graph on `n` edges and `n` vertices.
 */
fun findCycle(graph: Graph): List<Boolean> {
    val isInCycle = MutableList(graph.size) { false }

    val visited = MutableList(graph.size) { false }
    val parents = MutableList(graph.size) { -1 }
    val stack = mutableListOf<Int>()

    var startNode = -1
    var endNode = -1

    stack.add(0)
    while (stack.isNotEmpty()) {
        val node = stack.removeAt(0)
        if (!visited[node]) {
            visited[node] = true
            for (neighb in graph.neighbours(node)) {
                if (!visited[neighb]) {
                    parents[neighb] = node
                    stack.add(0, neighb)
                } else if (neighb != parents[node]) {
                    startNode = node
                    endNode = neighb

                    // To stop outer `while` have to clean stack.
                    stack.clear()
                    break
                }
            }
        }
    }

    assert(startNode != -1 || endNode != -1) {
        "Illegal algorithm state: probably a bad input graph."
    }

    isInCycle[startNode] = true
    while (startNode != endNode) {
        startNode = parents[startNode]
        isInCycle[startNode] = true
    }
    return isInCycle
}

/**
 * Find distances from each vertex of the graph to the closest vertices of the graph's cycle.
 */
fun findDistances(graph: Graph): List<Int> {
    val dists = MutableList(graph.size) { 0 }

    val cycle = findCycle(graph)

    fun dist(node: Int, parent: Int, curDist: Int) {
        dists[node] = curDist
        graph.neighbours(node).filter { idx -> idx != parent }
                .forEach { dist(it, node, curDist + 1) }
    }

    for (i in 0 until graph.size) {
        if (cycle[i]) {
            graph.neighbours(i).filter { node -> !cycle[node] }
                    .forEach { dist(it, i, 1) }
        }
    }

    return dists
}

fun main() {
    val sc = BufferedReader(InputStreamReader(System.`in`))
    val n: Int = sc.readLine().toInt()

    val graph = Graph(n)

    for (i in 0 until n) {
        val (from, to) = sc.readLine().split(" ").map { it.toInt() }
        graph.connectVertices(to - 1, from - 1)
    }

    findDistances(graph).forEach { print("$it ") }
}
