package ru.hse.spb

import java.util.*


fun main() {
    val scanner = Scanner(System.`in`)
    val flightsNumber = scanner.nextInt()
    val shift = scanner.nextInt()
    val costOfDelay = generateSequence { scanner.nextInt() }.take(flightsNumber).toList().toIntArray()
    val scheduler = Scheduler(costOfDelay)
    val newSchedule = scheduler.changeSchedule(shift)
}

class Scheduler(private val costOfDelay: IntArray) {

    private val flightsNumber = costOfDelay.size
    fun changeSchedule(shift: Int): List<Int> {
        val flightQueue = costOfDelay.take(shift).toSortedSet()
        val newSchedule = generateSequence { -1 }.take(flightsNumber).toMutableList()

        var currentIndex = shift
        while (flightQueue.isNotEmpty()) {
            if (currentIndex < flightsNumber) {
                flightQueue.add(costOfDelay[currentIndex])
            }

        }
        return newSchedule
    }

    fun calculateCostOfDaley(newSchedule: List<Int>): Long = newSchedule
            .map { it.toLong() }
            .withIndex()
            .zip(newSchedule)
            .map { (range, cost) ->
                val (newTime, originTime) = range
                (newTime - originTime) * cost
            }
            .sum()

}

data class Flights(val id: Int, val costOfDelay: Int)