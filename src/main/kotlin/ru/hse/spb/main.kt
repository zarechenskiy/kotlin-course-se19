package ru.hse.spb

/**
 * Class for storing weighted directed edges in graph.
 *
 * Default weight is 0
 */
data class Edge(val from: Int, val to: Int, var weight: Int = 0)

/**
 * Class for storing tree
 */
class Tree(private val size: Int) {
    private val adjacencyList = List(size) { mutableListOf<Edge>() }

    /** Adds bidirectional edge into graph */
    fun addEdge(aNode: Int, bNode: Int) {
        adjacencyList[aNode].add(Edge(aNode, bNode));
        adjacencyList[bNode].add(Edge(bNode, aNode))
    }

    /**
     * Finds centroid in a tree.
     *
     * @param vertexId vertex to start search with
     * @param parent parent of current vertex. Default -1
     * @return Pair<centroidIsFound, value>
     *     If centroidIsFound = true then value equals to centroid number,
     *     otherwise value equals to subtree size of current vertex.
     *     It is guaranteed that after execution from root,
     *     centroid will be found (i.e. value is equal to centroid index and centroidIsFound = true)
     */
    private fun findCentroidDfs(vertexId: Int, parent: Int = -1): Pair<Boolean, Int> {
        var subtreeSize = 1
        for (edge in adjacencyList[vertexId]) {
            if (edge.to != parent) {
                val (centroidIsFound, value) = findCentroidDfs(edge.to, vertexId)
                if (centroidIsFound) {
                    return Pair(centroidIsFound, value)
                }
                subtreeSize += value
            }
        }
        if (subtreeSize * 2 >= size) {
            return Pair(true, vertexId)
        }
        return Pair(false, subtreeSize)
    }

    /** Returns centroid of a tree */
    fun findCentroid(): Int {
        return findCentroidDfs(0).second
    }

    /** Returns subtree size of subtree with root {@code vertexId} and parent {@code parent}*/
    private fun getSubtreeSize(vertexId: Int, parent: Int = -1): Int {
        var size = 1
        for (edge: Edge in adjacencyList[vertexId]) {
            if (edge.to != parent) {
                size += getSubtreeSize(edge.to, vertexId)
            }
        }
        return size
    }

    /**
     * Returns copy of {@code vertexId} adjacency list where neighbours are sorted by
     *      increase of their subtree sizes
     */
    fun sortedSubtrees(vertexId: Int): List<Edge> {
        return adjacencyList[vertexId].sortedBy { getSubtreeSize(it.to, vertexId) }
    }

    /**
     * Paints current edge and all it's subtree below.
     *
     * Gives edges in subtree such weights, that exists paths with summary weights
     *      currentWeight, currentWeight + weightStep, currentWeight + 2 * weightStep...
     *
     * I.e. for tree
     *               |(1)
     *           (2)/\(3)
     *          (4)/\(5) with currentWeight = 1 and weightStep = 5:
     *
     *          (1) -- 1
     *          (2) -- 5 (weight (1) + (2) = 6)
     *          (4) -- 5 (weight (1) + (2) + (4) = 11)
     *          (5) -- 10 (weight (1) + (2) + (5) = 16)
     *          (3) -- 20 (weight (1) + (3) = 21)
     *          So we have summary weights 1, 6, 11, 16 and 21
     * @return last weight that was reached while painting current subtree
     */
    fun paint(edge: Edge, currentWeight: Int, weightStep: Int, previousWeight: Int = 0): Int {
        edge.weight = currentWeight - previousWeight
        var lastWeight = currentWeight
        for (nextEdge: Edge in adjacencyList[edge.to]) {
            if (nextEdge.to != edge.from) {
                lastWeight = paint(nextEdge, lastWeight + weightStep, weightStep,
                        currentWeight)
            }
        }
        return lastWeight
    }

    /**
     * Adds all edges in subtree of {@code vertexId} into given list.
     * Each edge occurs only once
     */
    fun getSubtreeEdges(vertexId: Int, list: MutableList<Edge>, parent: Int = -1) {
        for (edge: Edge in adjacencyList[vertexId]) {
            if (edge.to != parent) {
                list.add(edge)
                getSubtreeEdges(edge.to, list, vertexId)
            }
        }
    }
}

/** Solves the problem by given iterator to neighbours of centroid and tree */
fun paintAlmostAll(n: Int, neighbours: Iterator<Edge>, tree: Tree) {
    var sum = 0
    var neighbourId = 0
    while (sum * 3 < n + 1 && neighbours.hasNext()) {
        sum = tree.paint(neighbours.next(), sum + 1, 1)
    }
    val firstGroupSize = sum
    while (neighbours.hasNext()) {
        sum = tree.paint(neighbours.next(), sum + 1, firstGroupSize + 1)
        sum += firstGroupSize
        ++neighbourId
    }
}

fun main() {
    val n: Int = readInt()
    val tree = Tree(n)
    for (i in 1 until n) {
        var (u, v) = readInts()
        tree.addEdge(--u, --v)
    }
    val centroidIndex = tree.findCentroid()
    val neighbours: Iterator<Edge> = tree.sortedSubtrees(centroidIndex).iterator()
    paintAlmostAll(n, neighbours, tree)
    val edges = mutableListOf<Edge>()
    tree.getSubtreeEdges(centroidIndex, edges)
    for (edge in edges) {
        println("${edge.from + 1} ${edge.to + 1} ${edge.weight}")
    }
}

fun readTokens(): List<String> = readLine()!!.split(' ')
fun readInt(): Int = readLine()!!.toInt()
fun readInts(): List<Int> = readTokens().map { it.toInt() }