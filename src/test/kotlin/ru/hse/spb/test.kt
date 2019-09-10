package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.*

class TestSource {
    private fun testIO(input: String, output: String) {
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))
        System.setIn(ByteArrayInputStream(input.toByteArray()))

        main()
        assertEquals(output, out.toString())

        System.setIn(System.`in`)
        System.setOut(System.out)
    }

    @Test
    fun test1() {
        testIO("""
            2
            bac
            3
            2 a
            1 b
            2 c
        """.trimIndent(), "acb\n")
    }

    @Test
    fun test2() {
        testIO("""
            1
            abacaba
            4
            1 a
            1 a
            1 c
            2 b
        """.trimIndent(), "baa\n")
    }

    @Test
    fun test3() {
        testIO("""
            10
            bcbccaacab
            40
            37 c
            21 a
            18 a
            5 b
            1 a
            8 c
            9 a
            38 c
            10 b
            12 c
            18 a
            23 a
            20 c
            7 b
            33 c
            4 c
            22 c
            28 c
            9 a
            12 a
            22 a
            1 b
            6 a
            31 c
            19 b
            19 a
            15 a
            6 c
            11 c
            18 b
            19 c
            24 c
            8 a
            16 c
            2 c
            12 b
            8 a
            14 c
            18 b
            19 c
        """.trimIndent(), "cbcaabbccaaabbcccacabbccbbcbccabbcaacbbbcaacbccabbccaabbbcab\n")
    }
}
