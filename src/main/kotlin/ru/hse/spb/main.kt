package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.lang.Integer.max
import java.util.*
import kotlin.collections.ArrayList

data class Edge(val u: Int, val v: Int)

class DSU(vertexNumber: Int) {
    private var parent: IntArray = IntArray(vertexNumber) { x -> x }
    private var rank: IntArray = IntArray(vertexNumber) { 0 }
    var componentNumber: Int = vertexNumber
    var treeEdges: ArrayList<Edge> = ArrayList()

    fun root(u: Int): Int {
        if (parent[u] == u) {
            return u
        }
        parent[u] = root(parent[u])
        return parent[u]
    }

    fun unite(uVertex: Int, vVertex: Int) {
        var u = root(uVertex)
        var v = root(vVertex)
        if (u == v) {
            return
        }
        if (rank[u] > rank[v]) {
            u = v.also {v = u} // swap(u, v)
        }
        parent[u] = v
        rank[v] = max(rank[v], rank[u] + 1)
        componentNumber--
        treeEdges.add(Edge(uVertex, vVertex))
    }
}

data class Graph(
        val vertexNumber: Int,
        val edgeNumber: Int,
        val edges: List<Edge>,
        val sVertex: Int,
        val tVertex: Int,
        var sDegree: Int,
        var tDegree: Int
) {
    var DSU: DSU = DSU(vertexNumber)
}

fun readGraph(inputStream: InputStream): Graph {
    val scanner = Scanner(inputStream)
    val vertexNumber = scanner.nextInt()
    val edgeNumber = scanner.nextInt()
    return Graph(vertexNumber, edgeNumber, List(edgeNumber) { Edge(scanner.nextInt() - 1, scanner.nextInt() - 1) },
            scanner.nextInt() - 1, scanner.nextInt() - 1,
            scanner.nextInt(), scanner.nextInt())
}

fun uniteComponents(graph: Graph) {
    for (edge in graph.edges) {
        if (edge.u != graph.sVertex && edge.u != graph.tVertex
                && edge.v != graph.sVertex && edge.v != graph.tVertex) {
            graph.DSU.unite(edge.u, edge.v)
        }
    }
}

fun findEdgeToVertex(graph: Graph, u: Int): Array<Int?> {
    val connectingEdge: Array<Int?> = Array(graph.vertexNumber) { null }
    for (edge in graph.edges) {
        if (edge.u == u) {
            connectingEdge[graph.DSU.root(edge.v)] = edge.v
        }
        if (edge.v == u) {
            connectingEdge[graph.DSU.root(edge.u)] = edge.u
        }
    }
    return connectingEdge
}

fun connectLeafComponents(graph: Graph, sEdges: Array<Int?>, tEdges: Array<Int?>) {
    for (i in 0 until graph.vertexNumber) {
        val u = graph.DSU.root(i)
        if (u != i || u == graph.sVertex || u == graph.tVertex) {
            continue
        }
        if (sEdges[u] != null && tEdges[u] == null) {
            graph.DSU.unite(graph.sVertex, sEdges[u]!!)
            graph.sDegree--
        }
        if (tEdges[u] != null && sEdges[u] == null) {
            graph.DSU.unite(graph.tVertex, tEdges[u]!!)
            graph.tDegree--
        }
    }
}

fun connectAllWithSTEdge(graph: Graph, sEdges: Array<Int?>, tEdges: Array<Int?>) {
    graph.DSU.unite(graph.sVertex, graph.tVertex)
    graph.sDegree--
    graph.tDegree--
    for (u in 0 until graph.vertexNumber) {
        if (u != graph.DSU.root(u) || u == graph.sVertex || u == graph.tVertex) {
            continue
        }
        if (sEdges[u] != null && tEdges[u] != null) {
            if (graph.sDegree > 0) {
                graph.DSU.unite(graph.sVertex, sEdges[u]!!)
                graph.sDegree--
            } else {
                graph.DSU.unite(graph.tVertex, tEdges[u]!!)
                graph.tDegree--
            }
        }
    }
}

fun connectAllWithoutSTEdge(graph: Graph, sEdges: Array<Int?>, tEdges: Array<Int?>) {
    var isFirst = true
    for (u in 0 until graph.vertexNumber) {
        if (u != graph.DSU.root(u) || u == graph.sVertex || u == graph.tVertex)
            continue
        if (sEdges[u] != null && tEdges[u] != null) {
            if (isFirst) {
                graph.DSU.unite(graph.sVertex, sEdges[u]!!)
                graph.DSU.unite(graph.tVertex, tEdges[u]!!)
                graph.sDegree--
                graph.tDegree--
                isFirst = false
            } else if (graph.sDegree > 0) {
                graph.DSU.unite(graph.sVertex, sEdges[u]!!)
                graph.sDegree--
            } else {
                graph.DSU.unite(graph.tVertex, tEdges[u]!!)
                graph.tDegree--
            }
        }
    }
}

fun printAnswer(outputStream: OutputStream, graph: Graph, flag: Boolean) {
    val writer = PrintWriter(outputStream)
    if (!flag) {
        writer.println("No")
    } else {
        writer.println("Yes")
        for (edge in graph.DSU.treeEdges) {
            writer.printf("%d %d\n", edge.u + 1, edge.v + 1)
        }
    }
    writer.close()
}

fun solve(inputStream: InputStream, outputStream: OutputStream) {
    val graph = readGraph(inputStream)
    uniteComponents(graph)
    val sEdges = findEdgeToVertex(graph, graph.sVertex)
    val tEdges = findEdgeToVertex(graph, graph.tVertex)
    connectLeafComponents(graph, sEdges, tEdges)
    if (graph.sDegree + graph.tDegree >= graph.DSU.componentNumber && sEdges[graph.tVertex] != null) {
        connectAllWithSTEdge(graph, sEdges, tEdges)
    } else {
        connectAllWithoutSTEdge(graph, sEdges, tEdges)
    }
    if (graph.DSU.componentNumber == 1 && graph.sDegree >= 0 && graph.tDegree >= 0) {
        printAnswer(outputStream, graph, true)
    } else {
        printAnswer(outputStream, graph, false)
    }
}

fun main() {
    solve(System.`in`, System.out)
}