package ru.hse.spb

import java.util.*


fun main() {
    val scanner = Scanner(System.`in`.buffered())
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
        val flightQueue = PriorityQueue(compareBy<IndexedValue<Int>> { it.value }.thenBy { it.index }.reversed())

        for (currentIndex in 0 until flightsNumber + shift) {
            if (currentIndex < flightsNumber) {
                flightQueue.add(IndexedValue(currentIndex, costOfDelay[currentIndex]))
                if (currentIndex < shift) {
                    continue
                }
            }
            newSchedule[flightQueue.poll().index] = currentIndex + 1
        }
        return newSchedule
    }

    fun calculateSumCost(newSchedule: List<Int>): Long = newSchedule
            .asSequence()
            .withIndex()
            .map { (originTime, newTime) ->
                (newTime - originTime - 1) * costOfDelay[originTime].toLong()
            }
            .sum()

}