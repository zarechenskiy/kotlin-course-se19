package ru.hse.spb

import java.lang.Integer.max

class Solver {
    private val n: Int
    private val v: IntArray
    private val q: Int
    private val queries: Array<IntArray>
    val ans: IntArray

    constructor() {
        n = readLine()!!.toInt()
        v = readLine()!!.split(' ').map(String::toInt).toIntArray()
        q = readLine()!!.toInt()
        queries = Array(q) { readLine()!!.split(' ').map(String::toInt).toIntArray()}
        ans = IntArray(n)
    }

    internal constructor(n: Int, v: IntArray, q: Int, queries: Array<IntArray>) {
        this.n = n
        this.v = v
        this.q = q
        this.queries = queries
        ans = IntArray(n)
    }

    fun solve() {
        val last = List(n) {-1}.toMutableList()
        val ops: MutableList<Pair<Int,Int>> = emptyList<Pair<Int,Int>>().toMutableList()

        val get = fun (j: Int): Int {
            if (ops.size == 0)
                return v[j]
            var l = -1
            var r = ops.size
            while (r - l > 1) {
                val m = (l + r) / 2
                if (ops[m].first > last[j])
                    r = m
                else
                    l = m
            }
            return if (r == ops.size) v[j] else max(v[j], ops[r].second)
        }

        for (i in IntRange(0, q - 1)) {
            val cur = queries[i]
            if (cur[0] == 1) {
                val j = cur[1] - 1
                val x = cur[2]
                v[j] = x
                last[j] = i
            } else {
                val x = cur[1]
                while (ops.size > 0 && (ops.last().second < x))
                    ops.removeAt(ops.size - 1)
                ops.add(Pair(i, x))
            }
        }

        IntRange(0, n - 1).forEach { ans[it] = get(it) }
    }
}

fun main() {
    val solver = Solver()
    solver.solve()
    solver.ans.forEach { print("$it ") }
}
