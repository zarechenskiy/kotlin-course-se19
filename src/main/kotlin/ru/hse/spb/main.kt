package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*


typealias Vertex = Int
typealias Graph = Map<Vertex, List<Vertex>>


private fun findClosedPathImpl(
    currentVertex: Vertex,
    previousVertex: Vertex?,
    visitedVertex: MutableSet<Vertex>,
    path: MutableList<Vertex>,
    graph: Graph
): List<Vertex>? {
    path.add(currentVertex)
    if (currentVertex in visitedVertex) {
        return path
    }
    visitedVertex.add(currentVertex)
    for (neighbour in graph.getValue(currentVertex)) {
        if (neighbour == previousVertex) continue
        val rec = findClosedPathImpl(neighbour, currentVertex, visitedVertex, path, graph)
        if (rec != null) return rec
    }
    path.removeAt(path.lastIndex)
    return null
}

fun findClosedPath(startVertex: Vertex, graph: Graph): List<Vertex>? {
    return findClosedPathImpl(startVertex, null, mutableSetOf(), mutableListOf(), graph)
}

fun extractCycleFromClosedPath(path: List<Vertex>): Set<Vertex> {
    val cycle = mutableSetOf<Vertex>()
    for (vertex in path.reversed()) {
        if (vertex in cycle) break
        cycle.add(vertex)
    }
    return cycle
}

private fun findDistancesImpl(
    currentVertex: Vertex,
    previousVertex: Vertex?, cycle: Set<Vertex>, distances: MutableMap<Vertex, Int>, currentDistance: Int, graph: Graph
) {
    distances[currentVertex] = currentDistance
    for (neighbour in graph.getValue(currentVertex)) {
        if (neighbour == previousVertex) continue
        if (neighbour in cycle) continue
        findDistancesImpl(neighbour, currentVertex, cycle, distances, currentDistance + 1, graph)
    }
}

fun findDistances(cycle: Set<Vertex>, graph: Graph): Map<Vertex, Int> {
    val distances = mutableMapOf<Vertex, Int>()
    for (vertex in cycle) {
        findDistancesImpl(vertex, null, cycle, distances, 0, graph)
    }
    return distances
}

fun parseInput(inputStream: InputStream): Graph {
    val scanner = Scanner(inputStream)
    val size = scanner.nextInt()

    val graph = mutableMapOf<Vertex, MutableList<Vertex>>()

    for (i in 1..size) {
        graph[i] = mutableListOf()
    }

    repeat(size) {
        val i = scanner.nextInt()
        val j = scanner.nextInt()
        graph[i]?.add(j)
        graph[j]?.add(i)
    }

    return graph
}

fun putResult(distances: Map<Vertex, Int>, stream: OutputStream) {
    PrintWriter(stream).use {
        it.println(
            distances.entries.sortedBy { it.key }.map { it.value }.joinToString(separator = " ")
        )
    }
}

fun task(input: InputStream, output: OutputStream) {
    val subway = parseInput(input)
    val cycle = extractCycleFromClosedPath(
        findClosedPath(startVertex = 1, graph = subway)
            ?: error("The scheme must contain a cycle")
    )
    putResult(findDistances(cycle = cycle, graph = subway), output)
}

fun main() {
    task(System.`in`, System.out)
}