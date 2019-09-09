import java.math.BigInteger
import java.util.Scanner

fun main(args: Array<String>) {
    val scan = Scanner(System.`in`)
    val n = scan.nextInt()
    val arr = Array<Pair<Int, Pair<Int, Int>>>(n) { Pair(0, Pair(0, 0)) }
    for (i in 0 until n) arr[i] = Pair(i + 1, Pair(scan.nextInt(), scan.nextInt()))
    arr.sortWith((Comparator<Pair<Int, Pair<Int, Int>>> { x, y -> vectorsComp(x.second, y.second) }))
    var max = Pair<BigInteger, BigInteger>(BigInteger.valueOf((-1).toLong()), BigInteger.ONE)
    var ans = Pair(arr[0].first, arr[1].first)
    for (i in 0 until n) {
        val angleC = angleC(arr[i].second, arr[(i + 1) % n].second)
        if (angleC.first * max.second > angleC.second * max.first) {
            max = angleC
            ans = Pair(arr[i].first, arr[(i + 1) % n].first)
        }
    }
    println("${ans.first} ${ans.second}")
}

fun angleC(v1: Pair<Int, Int>, v2: Pair<Int, Int>): Pair<BigInteger, BigInteger> {
    val scProd = scProd(v1, v2)
    return Pair(scProd * scProd * scProd.signum().toBigInteger(), module(v1) * module(v2))
}

fun scProd(v1: Pair<Int, Int>, v2: Pair<Int, Int>): BigInteger {
    return (v1.first * v2.first + v1.second * v2.second).toBigInteger()
}

fun module(v: Pair<Int, Int>): BigInteger {
    return scProd(v, v)
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