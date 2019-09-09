package ru.hse.spb

import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

fun dist(x1: Double, y1: Double, x2: Double, y2: Double) = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))

fun readDoubles(reader: BufferedReader) = reader.readLine()!!.split(" ").map { it.toDouble() }

fun solver(inputStream: InputStream): Double {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val (x1, y1, x2, y2) = readDoubles(reader)
    val (vMax, t) = readDoubles(reader)
    val (vx, vy) = readDoubles(reader)
    val (wx, wy) = readDoubles(reader)
    reader.close()

    var l = 0.0
    var r = 1e9
    for (i in 1..200) {
        val mid: Double = (l + r) / 2

        val p1: Double
        val p2: Double
        if (mid <= t) {
            p1 = mid
            p2 = 0.0
        } else {
            p1 = t
            p2 = mid - t
        }

        val tx = x2 - p1 * vx - p2 * wx
        val ty = y2 - p1 * vy - p2 * wy

        if (vMax * mid >= dist(tx, ty, x1, y1)) {
            r = mid
        } else {
            l = mid
        }
    }
    return l
}

fun main() {
    println(solver(System.`in`))
}