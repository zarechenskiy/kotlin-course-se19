package ru.hse.spb

import java.lang.StrictMath.*
import java.util.*

const val epsilon: Double = 1e-7

class Point(val x: Double = 0.0, val y: Double = 0.0) {
    fun distanceTo(other: Point): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
    }
}

fun isReachable(start: Point, finish: Point,
                coordinateDifferenceX: Double, coordinateDifferenceY: Double,
                time: Double, speed: Double): Boolean {
    val finishConsideringWind = Point(finish.x - coordinateDifferenceX, finish.y - coordinateDifferenceY)
    return speed * time >= start.distanceTo(finishConsideringWind)
}

fun solve(input: Scanner): Double {
    val start = Point(input.nextDouble(), input.nextDouble())
    val finish = Point(input.nextDouble(), input.nextDouble())
    val speed = input.nextDouble()
    val time = input.nextDouble()
    val firstWindSpeedX = input.nextDouble()
    val firstWindSpeedY = input.nextDouble()
    val secondWindSpeedX = input.nextDouble()
    val secondWindSpeedY = input.nextDouble()

    var low = 0.0
    var high = 1e18
    while (high - low > epsilon) {
        val middle = (low + high) / 2
        val coordinateDifferenceX = firstWindSpeedX * min(time, middle) + secondWindSpeedX * max(0.0, middle - time)
        val coordinateDifferenceY = firstWindSpeedY * min(time, middle) + secondWindSpeedY * max(0.0, middle - time)
        if (isReachable(start, finish, coordinateDifferenceX, coordinateDifferenceY, middle, speed)) high = middle
        else low = middle
    }

    return low
}

fun main() {
    println(solve(Scanner(System.`in`)))
}