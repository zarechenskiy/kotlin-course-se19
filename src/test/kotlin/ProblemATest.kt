import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.ProblemA

class ProblemATest {

    private fun check(r: IntArray, result: Pair<Pair<Int, Int>, Int>): Boolean {
        val n = r.size
        val (a, b) = result.first
        val c = result.second
        if (a < 1 || a > n || b < 1 || b > n || c < 1 || c > n) {
            return false
        }
        if (a == b || a == c || b == c) {
            return false
        }
        return r[a - 1] < r[b - 1] && r[b - 1] < r[c - 1]
    }

    private fun validate(r: IntArray, isSolutionExists: Boolean = true) {
        val result = ProblemA.solve(r)
        if (isSolutionExists) {
            assertTrue(check(r, result))
        } else {
            assertEquals(Pair(Pair(-1, -1), -1), result)
        }
    }

    @Test
    fun sample1() {
        validate(intArrayOf(3, 1, 4, 1, 5, 9))
    }

    @Test
    fun sample2() {
        validate(intArrayOf(1, 1000000000, 1, 1000000000, 1), false)
    }


    @Test
    fun sample3() {
        validate(intArrayOf(10, 10, 11, 10, 10, 10, 10, 10, 1))
    }
}
