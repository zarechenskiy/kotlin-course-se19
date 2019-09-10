package ru.hse.spb

import java.util.*


fun main() {
    val scanner = Scanner(System.`in`)
    val flightsNumber = scanner.nextInt()
    val shift = scanner.nextInt()
    val costOfDelay = generateSequence { scanner.nextInt() }.take(flightsNumber).toList().toTypedArray()

    val scheduler = Scheduler(costOfDelay)
    val newSchedule = scheduler.changeSchedule(shift)
    val sum = scheduler.calculateSumCost(newSchedule)

    println(sum)
    println(newSchedule.joinToString(" "))
}

class Scheduler(private val costOfDelay: Array<Int>) {

    private val flightsNumber = costOfDelay.size
    fun changeSchedule(shift: Int): List<Int> {
        val newSchedule = generateSequence { -1 }.take(flightsNumber).toMutableList()
        val flightQueue = costOfDelay.take(shift).withIndex().toSortedSet(compareBy<IndexedValue<Int>> { it.value }.thenBy { it.index })

        var currentIndex = shift
        while (flightQueue.isNotEmpty()) {
            if (currentIndex < flightsNumber) {
                flightQueue.add(IndexedValue(currentIndex, costOfDelay[currentIndex]))
            }
            val currentFlight = flightQueue.last()
            flightQueue.remove(currentFlight)
            newSchedule[currentFlight.index] = ++currentIndex
        }
        return newSchedule
    }

    fun calculateSumCost(newSchedule: List<Int>): Long = newSchedule
            .map { it.toLong() }
            .withIndex()
            .zip(costOfDelay)
            .map { (range, cost) ->
                val (originTime, newTime) = range
                (newTime - originTime - 1) * cost
            }
            .sum()

}

data class Flights(val id: Int, val costOfDelay: Int)