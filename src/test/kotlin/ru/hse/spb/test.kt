package ru.hse.spb

import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.StrictMath.abs
import java.lang.StrictMath.max
import java.util.*

class TestSource {

    private fun checkAnswer(expected: Double, actual: Double): Boolean {
        return abs(actual - expected) / max(1.0, expected) <= 1e-6
    }

    @Test
    fun testSolutionBySample1() {
        val sample = "0 0 5 5\n" +
                "3 2\n" +
                "-1 -1\n" +
                "-1 0"
        val expectedAnswer = 3.729935587093555327
        assertTrue(checkAnswer(solve(Scanner(sample)), expectedAnswer))
    }

    @Test
    fun testSolutionBySample2() {
        val sample = "0 0 0 1000\n" +
                "100 1000\n" +
                "-50 0\n" +
                "50 0"
        val expectedAnswer = 11.547005383792516398
        assertTrue(checkAnswer(solve(Scanner(sample)), expectedAnswer))
    }

}