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

fun minimalRoadsLength(edgeList: Array<MutableList<Int>>, universities: List<Int>, isUniversity: BooleanArray): Int {
    val n = edgeList.size
    val subLength = IntArray(n)
    val subCount = IntArray(n)

    universities.forEach { subCount[it] = 1 }

    calculateSubtreeParams(0, -1, edgeList, subLength, subCount)
    val v = findBestAnswer(0, -1, edgeList, universities.size, subLength, subCount, 0)
    return sumDistances(v, -1, 0, edgeList, isUniversity)
}

fun calculateSubtreeParams(v: Int, parent: Int, edgeList: Array<MutableList<Int>>, subLength: IntArray, subCount: IntArray) {
    for (u in edgeList[v]) {
        if (u == parent) {
            continue
        }
        calculateSubtreeParams(u, v, edgeList, subLength, subCount)
        subCount[v] += subCount[u]
        subLength[v] += subLength[u] + subCount[u]
    }
}

fun findBestAnswer(v: Int, parent: Int, edgeList: Array<MutableList<Int>>, universitiesCount: Int, subLength: IntArray, subCount: IntArray, upLength: Int) : Int {

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
        val t = findBestAnswer(u, v, edgeList, universitiesCount, subLength, subCount, upLength)
        if (t != -1) {
            return t
        }
    }
    return -1
}

fun sumDistances(v : Int, parent : Int, depth : Int, edgeList : Array<MutableList<Int>>, isUniversity : BooleanArray) : Int {
    var ans = if (isUniversity[v]) depth else 0
    for (u in edgeList[v]) {
        if (u == parent) {
            continue
        }
        ans += sumDistances(u, v, depth + 1, edgeList, isUniversity)
    }
    return ans
}

fun main() {
    val (n, k) = readLine()!!.split(' ').map(String::toInt)
    val universities = readLine()!!.split(' ').map { it.toInt() - 1 }
    val edgeList = readEdgeList(n, n - 1)

    val isUniversity = BooleanArray(n)
    universities.forEach { isUniversity[it] = true }

    println(minimalRoadsLength(edgeList, universities, isUniversity))
}
