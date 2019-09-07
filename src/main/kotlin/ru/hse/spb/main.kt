package ru.hse.spb

import java.io.InputStream
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

data class Vector2(val x: Number, val y: Number) {
    operator fun plus(v2: Vector2) = Vector2(
            v2.x.toDouble() + x.toDouble(),
            v2.y.toDouble() + y.toDouble()
    )

    operator fun minus(v2: Vector2) = Vector2(
            x.toDouble() - v2.x.toDouble(),
            y.toDouble() - v2.y.toDouble()
    )

    val length: Double
        get() {
            return sqrt(x.toDouble() * x.toDouble() + y.toDouble() * y.toDouble())
        }

    operator fun times(t: Number) = Vector2(
            t.toDouble() * x.toDouble(), t.toDouble() * y.toDouble()
    )

    companion object {
        fun read(scanner: Scanner): Vector2 {
            val x = scanner.nextInt()
            val y = scanner.nextInt()
            return Vector2(x, y)
        }
    }
}

class Wind(val direction: Vector2, val timeStart: Double, val timeEnd: Double)

fun binarySearch(from: Double, to: Double, criterion: (Double) -> Double,
                 eps: Double = 0.0000001): Double {
    var curFrom = from
    var curTo = to
    var curValue = (curFrom + curTo) / 2
    var curCriterion = criterion(curValue)
    while (abs(curCriterion) > eps) {
        curValue = (curFrom + curTo) / 2
        curCriterion = criterion(curValue)
        if (curCriterion > 0) {
            curFrom = curValue
        } else {
            curTo = curValue
        }
    }
    return curValue
}

data class TaskInput(val from: Vector2, val to: Vector2, val maxSpeed: Int, val winds: List<Wind>)

class TaskSolver(private val taskInput: TaskInput,
                 val minDistance: Double = 0.0,
                 val maxDistance: Double = 1e9) {
    fun solve(): Double {
        val criterion = { time: Double ->
            var windCoordinates = taskInput.from
            for (wind in taskInput.winds) {
                if (wind.timeStart <= time && time < wind.timeEnd) {
                    windCoordinates += wind.direction * (time - wind.timeStart)
                }
                if (wind.timeStart <= time && time >= wind.timeEnd) {
                    windCoordinates += wind.direction * (wind.timeEnd - wind.timeStart)
                }
            }
            (taskInput.to - windCoordinates).length - taskInput.maxSpeed * time
        }
        val time = binarySearch(minDistance, maxDistance, criterion)
        return time
    }
}

fun parseInput(inputStream: InputStream): TaskInput {
    val scanner = Scanner(inputStream)
    val from = Vector2.read(scanner)
    val to = Vector2.read(scanner)
    scanner.nextLine()
    val (speed, windChangingTime) = scanner.nextLine().split(' ').map(String::toInt)
    val windList = listOf(
            Wind(Vector2.read(scanner), 0.0, windChangingTime.toDouble()),
            Wind(Vector2.read(scanner), windChangingTime.toDouble(), Double.POSITIVE_INFINITY)
    )
    return TaskInput(from, to, speed, windList)
}

fun main() {
    val taskInput = parseInput(System.`in`)
    val solver = TaskSolver(taskInput)
    println(solver.solve())
}