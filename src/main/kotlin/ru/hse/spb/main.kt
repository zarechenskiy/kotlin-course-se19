package ru.hse.spb

import kotlin.math.max

fun readEdgeList(vertexNumber: Int, edgeNumber: Int): Array<List<Int>> {
    val mutableEdgeList = Array(vertexNumber) { MutableList(0) { 0 } }
    for (i in 0 until edgeNumber) {
        val (vertex1, vertex2) = readLine()!!.split(' ').map { it.toInt() - 1 }
        mutableEdgeList[vertex1].add(vertex2)
        mutableEdgeList[vertex2].add(vertex1)
    }
    return Array(vertexNumber) { mutableEdgeList[it] }
}

fun maximalRoadsLength(edgeList: Array<List<Int>>, universities: List<Int>): Long {
    val solver = MaximalRoadsLengthFinder(edgeList, universities)
    return solver.solveProblem()
}

class MaximalRoadsLengthFinder(private val edgeList: Array<List<Int>>, private val universities: List<Int>) {

    private val subCount = IntArray(edgeList.size)
    private val isUniversity = BooleanArray(edgeList.size)

    init {
        universities.forEach { subCount[it] = 1 }
        universities.forEach { isUniversity[it] = true }
    }

    fun solveProblem(): Long {

        calculateSubtreeParams(0, -1)
        val v = findBestAnswer(0, -1)
        return sumDistances(v, -1, 0)
    }

    private inline fun doForChildren(v: Int, parent: Int, whatToDo: (Int) -> Unit) {
        edgeList[v].forEach { if (it != parent) whatToDo(it) }
    }

    private fun calculateSubtreeParams(v: Int, parent: Int) {
        doForChildren(v, parent) { u ->
            calculateSubtreeParams(u, v)
            subCount[v] += subCount[u]
        }
    }

    private fun findBestAnswer(v: Int, parent: Int): Int {
        val universitiesCount = universities.size
        var maxSubtreeSize = universitiesCount - subCount[v] // count of vertices up from v
        doForChildren(v, parent) { maxSubtreeSize = max(maxSubtreeSize, subCount[it]) }
        if (maxSubtreeSize * 2 <= universitiesCount) {
            return v
        }

        for (u in edgeList[v]) {
            if (u == parent) {
                continue
            }
            val t = findBestAnswer(u, v)
            if (t != -1) {
                return t
            }
        }
        return -1
    }

    private fun sumDistances(v: Int, parent: Int, depth: Int): Long {
        var ans: Long = if (isUniversity[v]) depth.toLong() else 0
        doForChildren(v, parent) { ans += sumDistances(it, v, depth + 1) }
        return ans
    }
}

fun main() {
    val (n, _) = readLine()!!.split(' ').map(String::toInt)
    val universities = readLine()!!.split(' ').map { it.toInt() - 1 }
    val edgeList = readEdgeList(n, n - 1)

    println(maximalRoadsLength(edgeList, universities))
}
