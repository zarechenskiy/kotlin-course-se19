import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.ProblemA
import ru.hse.spb.Triple

class ProblemATest {

    private fun check(ratings: List<Int>, result: Triple): Boolean {
        val tasksNumber = ratings.size
        val (leftIndex, middleIndex, rightIndex) = result
        if (leftIndex < 1 || leftIndex > tasksNumber
            || middleIndex < 1 || middleIndex > tasksNumber
            || rightIndex < 1 || rightIndex > tasksNumber) {
            return false
        }
        if (leftIndex == middleIndex || leftIndex == rightIndex || middleIndex == rightIndex) {
            return false
        }
        return ratings[leftIndex - 1] < ratings[middleIndex - 1]
                && ratings[middleIndex - 1] < ratings[rightIndex - 1]
    }

    private fun validate(ratings: List<Int>, isSolutionExists: Boolean = true) {
        val result = ProblemA.solve(ratings)
        if (isSolutionExists) {
            assertTrue(check(ratings, result))
        } else {
            assertEquals(Triple(-1, -1, -1), result)
        }
    }

    @Test
    fun sample1() {
        validate(listOf(3, 1, 4, 1, 5, 9))
    }

    @Test
    fun sample2() {
        validate(listOf(1, 1000000000, 1, 1000000000, 1), false)
    }


    @Test
    fun sample3() {
        validate(listOf(10, 10, 11, 10, 10, 10, 10, 10, 1))
    }
}
