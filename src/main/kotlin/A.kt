package ru.hse.spb

data class Triple(val first: Int, val second: Int, val third: Int) {
    override fun toString(): String {
        return "$first $second $third"
    }
}

/**
 * Object solver for problem A of (https://codeforces.com/contest/1211)
 * submit: https://codeforces.com/contest/1211/submission/60338534
 * */
object ProblemA {
    /**
     * @param ratings is the complexity of the tasks
     * @return triple according to the statement of the problem
     * (https://codeforces.com/contest/1211/problem/A)
     * */
    fun solve(ratings: List<Int>): Triple {
        val tasksNumber = ratings.size
        var maxIndex = -1
        var minIndex = -1
        for (i in 0 until tasksNumber) {
            if (maxIndex == -1 || ratings[i] > ratings[maxIndex]) {
                maxIndex = i
            }
            if (minIndex == -1 || ratings[i] < ratings[minIndex]) {
                minIndex = i
            }
        }
        for (i in 0 until tasksNumber) {
            if (i == minIndex || i == maxIndex) {
                continue
            }
            if (ratings[minIndex] < ratings[i] && ratings[i] < ratings[maxIndex]) {
                return Triple(minIndex + 1, i + 1, maxIndex + 1)
            }
        }
        return Triple(-1, -1, -1)
    }
}

fun main() {
    readLine()!!.toInt()
    val ratings: List<Int> = readLine()!!.split(" ").map { it.toInt() }
    val result = ProblemA.solve(ratings)
    println(result)
}
