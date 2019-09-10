package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SchedulerTest {

    @Test
    fun changeScheduleTest() {
        val k = 2
        val costOfDelay = arrayOf(4, 2, 1, 10, 2)

        val scheduler = Scheduler(costOfDelay)

        val expectedSchedule = listOf(3, 6, 7, 4, 5)
        assertEquals(expectedSchedule, scheduler.changeSchedule(k))
        assertEquals(20, scheduler.calculateSumCost(expectedSchedule))
    }
}