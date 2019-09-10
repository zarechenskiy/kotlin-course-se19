package ru.hse.spb

/**
 * Object solver for problem A of (https://codeforces.com/contest/1211)
 * accepted submit: https://codeforces.com/contest/1211/submission/60338534
 * */
object ProblemA {
    /**
     * @param r is the complexity of the tasks
     * @return triple according to the statement of the problem
     * (https://codeforces.com/contest/1211/problem/A)
     * */
    fun solve(r: IntArray): Pair<Pair<Int, Int>, Int> {
        val n = r.size
        var maxIndex = -1
        var minIndex = -1
        for (i in 0 until n) {
            if (maxIndex == -1 || r[i] > r[maxIndex]) {
                maxIndex = i
            }
            if (minIndex == -1 || r[i] < r[minIndex]) {
                minIndex = i
            }
        }
        for (i in 0 until n) {
            if (i == minIndex || i == maxIndex) {
                continue
            }
            if (r[minIndex] < r[i] && r[i] < r[maxIndex]) {
                return Pair(Pair(minIndex + 1, i + 1), maxIndex + 1)
            }
        }
        return Pair(Pair(-1, -1), -1)
    }
}

fun main() {
    readLine()!!.toInt()
    val r: IntArray = readLine()!!.split(" ").map { it.toInt() }.toIntArray()
    val result = ProblemA.solve(r)
    println("${result.first.first} ${result.first.second} ${result.second}")
}
