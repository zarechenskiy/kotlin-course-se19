package ru.hse.spb

import kotlin.math.max

fun readEdgeList(vertexNumber: Int, edgeNumber: Int): Array<MutableList<Int>> {
    val edgeList = Array(vertexNumber) { MutableList(0) { 0 } }
    for (i in 0 until edgeNumber) {
        val (vertex1, vertex2) = readLine()!!.split(' ').map { it.toInt() - 1 }
        edgeList[vertex1].add(vertex2)
        edgeList[vertex2].add(vertex1)
    }
    return edgeList
}

fun maximalRoadsLength(edgeList: Array<out List<Int>>, universities: List<Int>): Long {
    val solver = MaximalRoadsLengthFinder(edgeList, universities)
    return solver.solveProblem()
}

class MaximalRoadsLengthFinder private constructor(private val edgeList: Array<out List<Int>>, private val universities: List<Int>, private val subCount : IntArray, private val isUniversity: BooleanArray) {

    constructor(edgeList: Array<out List<Int>>, universities: List<Int>) : this(edgeList, universities, IntArray(edgeList.size), BooleanArray(edgeList.size)) {
        universities.forEach { subCount[it] = 1 }
        universities.forEach { isUniversity[it] = true }
    }

    fun solveProblem() : Long {
        calculateSubtreeParams(0, -1)
        val v = findBestAnswer(0, -1)
        return sumDistances(v, -1, 0)
    }

    private fun calculateSubtreeParams(v: Int, parent: Int) {
        for (u in edgeList[v]) {
            if (u == parent) {
                continue
            }
            calculateSubtreeParams(u, v)
            subCount[v] += subCount[u]
        }
    }

    private fun findBestAnswer(v: Int, parent: Int) : Int {
        val universitiesCount = universities.size
        var maxSubtreeSize = universitiesCount - subCount[v] // count of vertices up from v
        for (u in edgeList[v]) {
            if (u == parent) {
                continue
            }
            maxSubtreeSize = max(maxSubtreeSize, subCount[u])
        }
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

    private fun sumDistances(v : Int, parent : Int, depth : Int) : Long {
        var ans : Long = if (isUniversity[v]) depth.toLong() else 0
        for (u in edgeList[v]) {
            if (u == parent) {
                continue
            }
            ans += sumDistances(u, v, depth + 1)
        }
        return ans
    }
}



fun main() {
    val (n, _) = readLine()!!.split(' ').map(String::toInt)
    val universities = readLine()!!.split(' ').map { it.toInt() - 1 }
    val edgeList = readEdgeList(n, n - 1)

    println(maximalRoadsLength(edgeList, universities))
}
