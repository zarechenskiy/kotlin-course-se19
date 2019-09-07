package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun firstTestFromCF() {
        val from = Vector2(0, 0)
        val to = Vector2(5, 5)
        val maxSpeed = 3
        val windChanges = 2.0
        val windList = listOf(
                Wind(Vector2(-1, -1), 0.0, windChanges),
                Wind(Vector2(-1, 0), windChanges, Double.POSITIVE_INFINITY)
        )
        val solver = TaskSolver(TaskInput(from, to, maxSpeed, windList))
        assertEquals(3.729935587093555327, solver.solve(), 0.000001)
    }

    @Test
    fun secondTestFromCF() {
        val from = Vector2(0, 0)
        val to = Vector2(0, 1000)
        val maxSpeed = 100
        val windChanges = 1000.0
        val windList = listOf(
                Wind(Vector2(-50, 0), 0.0, windChanges),
                Wind(Vector2(50, 0), windChanges, Double.POSITIVE_INFINITY)
        )
        val solver = TaskSolver(TaskInput(from, to, maxSpeed, windList))
        assertEquals(11.547005383792516398, solver.solve(), 0.000001)
    }

}