package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.math.BigInteger
import java.util.Scanner
fun solveTask(input: InputStream, output: OutputStream) {
    val scan = Scanner(input)
    val n = scan.nextInt()
    val data = Array<Pair<Int, Pair<Int, Int>>>(n) { Pair(0, Pair(0, 0)) }
    for (i in 0 until n) 
        data[i] = Pair(i + 1, Pair(scan.nextInt(), scan.nextInt()))
    data.sortWith((Comparator<Pair<Int, Pair<Int, Int>>> { x, y -> vectorsComp(x.second, y.second) }))
    var maxCos = Pair<BigInteger, BigInteger>(BigInteger.valueOf((-1).toLong()), BigInteger.ONE)
    var answer = Pair(data[0].first, data[1].first)
    for (i in 0 until n) {
        val angleCos = angleCos(data[i].second, data[(i + 1) % n].second)
        if (angleCos.first * maxCos.second > angleCos.second * maxCos.first) {
            maxCos = angleCos
            answer = Pair(data[i].first, data[(i + 1) % n].first)
        }
    }
    val writer = PrintWriter(output)
    writer.print("${answer.first} ${answer.second}")
    writer.close()
}

fun main(args: Array<String>) {
    solveTask(System.`in`, System.out)
}

fun angleCos(v1: Pair<Int, Int>, v2: Pair<Int, Int>): Pair<BigInteger, BigInteger> {
    val scalarProduct = scalarProduct(v1, v2)
    return Pair(scalarProduct * scalarProduct * scalarProduct.signum().toBigInteger(), module(v1) * module(v2))
}

fun scalarProduct(v1: Pair<Int, Int>, v2: Pair<Int, Int>): BigInteger {
    return (v1.first * v2.first + v1.second * v2.second).toBigInteger()
}

fun module(v: Pair<Int, Int>): BigInteger {
    return scalarProduct(v, v)
}

fun vectorsComp(v1: Pair<Int, Int>, v2: Pair<Int, Int>): Int {
    var sign = v1.second * v2.first - v1.first * v2.second
    if (sign == 0) {
        if ((v1.first * v2.first < 0 || v1.second * v2.second < 0))
            sign = if (v2.second != 0) v2.second else v2.first
    } else
        if (sign < 0) {
            if (v1.second < 0 && v2.second >= 0) sign = 1
        } else {
            if (v1.second >= 0 && v2.second < 0) sign = -1
        }
    return sign
}