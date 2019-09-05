package ru.hse.spb

import java.lang.StrictMath.*
import java.util.*

var epsilon: Double = 1e-7

class Point(var x: Double = 0.0, var y: Double = 0.0)

fun distance(first: Point, second: Point): Double {
    return sqrt((first.x - second.x) * (first.x - second.x) + (first.y - second.y) * (first.y - second.y))
}

fun isReachable(start: Point, finish: Point,
                coordinateDifferenceX: Double, coordinateDifferenceY: Double,
                time: Double, speed: Double): Boolean {
    val finishConsideringWind = Point(finish.x - coordinateDifferenceX, finish.y - coordinateDifferenceY)
    return speed * time >= distance(start, finishConsideringWind)
}

fun solve(input: Scanner): Double {
    val start = Point()
    start.x = input.nextDouble()
    start.y = input.nextDouble()
    val finish = Point()
    finish.x = input.nextDouble()
    finish.y = input.nextDouble()
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