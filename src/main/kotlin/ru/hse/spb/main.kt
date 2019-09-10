package ru.hse.spb

import java.math.BigInteger
import java.util.*
import kotlin.Comparator

data class PlaneInfo(val delayCost: Int, val index: Int)
data class DepartureOrder(val totalCost: BigInteger, val planesIndexes: List<Int>)

private fun multiplyInBigInt(fisrt: Int, second: Int): BigInteger {
    return BigInteger.valueOf((fisrt * second).toLong())
}

fun solve(planesNumber: Int, delay: Int, list: List<Int>): DepartureOrder {
    val planesList = list.indices.map { PlaneInfo(list[it], it) }

    val setOfAvailablePlanes = TreeSet(planesList.take(delay).toSortedSet(object : Comparator<PlaneInfo> {
        override fun compare(o1: PlaneInfo, o2: PlaneInfo): Int {
            val delayDist = o1.delayCost - o2.delayCost
            val indexDist = o1.index - o2.index
            if (delayDist != 0) {
                return delayDist
            }

            return indexDist
        }
    }))
    val departureTime = IntArray(planesNumber)
    var resultSum = BigInteger.ZERO

    for (time in (delay + 1..planesNumber)) {
        setOfAvailablePlanes.add(planesList[time - 1])

        val mostExpesivePlane = setOfAvailablePlanes.last()
        resultSum += multiplyInBigInt(mostExpesivePlane.delayCost, time - mostExpesivePlane.index - 1)
        setOfAvailablePlanes.remove(mostExpesivePlane)
        departureTime[mostExpesivePlane.index] = time
    }

    for (time in (planesNumber + 1..delay + planesNumber)) {
        val mostExpesivePlane = setOfAvailablePlanes.last()
        resultSum += multiplyInBigInt(mostExpesivePlane.delayCost, time - mostExpesivePlane.index - 1)
        setOfAvailablePlanes.remove(mostExpesivePlane)
        departureTime[mostExpesivePlane.index] = time
    }

    return DepartureOrder(resultSum, departureTime.toList())
}

fun main() {
    val scanner = Scanner(System.`in`)
    val number = scanner.nextInt()
    val delay = scanner.nextInt()
    val costs = (1..number).map { scanner.nextInt() }

    val result = solve(number, delay, costs)

    println(result.totalCost)
    println(result.planesIndexes.joinToString(separator = " "))
}