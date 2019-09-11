package ru.hse.spb

import java.lang.Integer.max

class Solver (
    private val n: Int,
    private val v: IntArray,
    private val q: Int,
    private val queries: Array<IntArray>,
    val ans: IntArray
) {
    fun solve() {
        val last = MutableList(n) { -1 }
        val ops = mutableListOf<Pair<Int,Int>>()

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

        for (i in 0 until q) {
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

        for (i in 0 until n) {
            ans[i] = get(i)
        }
    }
}

fun main() {
    val n = readLine()!!.toInt()
    val v = readLine()!!.split(' ').map(String::toInt).toIntArray()
    val q = readLine()!!.toInt()
    val queries = Array(q) { readLine()!!.split(' ').map(String::toInt).toIntArray()}
    val ans = IntArray(n)

    val solver = Solver(n, v, q, queries, ans)
    solver.solve()
    println(solver.ans.joinToString(" "))
}
