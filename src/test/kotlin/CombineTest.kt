package ru.hse.spb

import org.junit.Test

class CombineTest {

    @Test
    fun ifScenario() {
        val code =
            "var a = 10" +
            "var b = 20" +
            "if (a > b) {" +
                "println(1)" +
            "} else {" +
                "println(0)" +
            "}"
    }
}
