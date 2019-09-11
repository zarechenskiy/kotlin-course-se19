package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.math.BigInteger
import java.util.Scanner

open class Vector(val x: Int, val y: Int)

class IndexedVector(val index: Int, x: Int, y: Int) : Vector(x, y)

fun getData(input: InputStream): Array<IndexedVector> {
    val scan = Scanner(input)
    val n = scan.nextInt()
    val data = Array<IndexedVector>(n) { IndexedVector(0, 0, 0) }
    for (i in 0 until n)
        data[i] = IndexedVector(i + 1, scan.nextInt(), scan.nextInt())
    return data
}

fun printAnswer(output: OutputStream, answer: String) {
    val writer = PrintWriter(output)
    writer.print(answer)
    writer.close()
}

fun solveTask(input: InputStream, output: OutputStream) {
    val data = getData(input)
    val n = data.size
    data.sortWith(Comparator { x, y -> vectorsComp(x, y) })
    var maxCos = Pair<BigInteger, BigInteger>(-BigInteger.ONE, BigInteger.ONE)
    var answer = Pair(data[0].index, data[1].index)
    for (i in 0 until n) {
        val angleCos = angleCos(data[i], data[(i + 1) % n])
        if (angleCos.first * maxCos.second > angleCos.second * maxCos.first) {
            maxCos = angleCos
            answer = Pair(data[i].index, data[(i + 1) % n].index)
        }
    }
    printAnswer(output, "${answer.first} ${answer.second}")
}

fun main(args: Array<String>) {
    solveTask(System.`in`, System.out)
}

fun angleCos(v1: Vector, v2: Vector): Pair<BigInteger, BigInteger> {
    val scalarProduct = scalarProduct(v1, v2)
    return Pair(scalarProduct * scalarProduct * scalarProduct.signum().toBigInteger(), module(v1) * module(v2))
}

fun scalarProduct(v1: Vector, v2: Vector): BigInteger {
    return (v1.x * v2.x + v1.y * v2.y).toBigInteger()
}

fun module(v: Vector): BigInteger {
    return scalarProduct(v, v)
}

fun vectorsComp(v1: Vector, v2: Vector): Int {
    var sign = v1.y * v2.x - v1.x * v2.y
    if (sign == 0) {
        if ((v1.x * v2.x < 0 || v1.y * v2.y < 0))
            sign = if (v2.y != 0) v2.y else v2.x
    } else
        if (sign < 0) {
            if (v1.y < 0 && v2.y >= 0) sign = 1
        } else {
            if (v1.y >= 0 && v2.y < 0) sign = -1
        }
    return sign
}