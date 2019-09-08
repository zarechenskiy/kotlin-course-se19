package tanvd.itmo.kotlin

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun `first test from codeforces`() {
        val input = """
            2
            bac
            3
            2 a
            1 b
            2 c
            """.trimIndent()
        assertEquals("acb", Solver.solve(input.byteInputStream()))
    }

    @Test
    fun `second test from codeforces`() {
        val input = """
            1
            abacaba
            4
            1 a
            1 a
            1 c
            2 b
            """.trimIndent()
        assertEquals("baa", Solver.solve(input.byteInputStream()))
    }
}
