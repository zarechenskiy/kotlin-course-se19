import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.Tree

class TreeTest {

    @Test
    fun sample1() {
        val vertexNumber = 7
        val colors = intArrayOf(2, 3, 2, 7, 1, 1, 3)
        val targetColors = intArrayOf(7, 1, 2, 3, 1, 2, 3)
        val edges = arrayListOf(
            Pair(1, 7),
            Pair(4, 1),
            Pair(2, 6),
            Pair(2, 3),
            Pair(2, 4),
            Pair(5, 4)
        )
        val tree = Tree(vertexNumber, colors, targetColors, edges)
        val path = tree.findKingPath()!!.toIntArray()
        assertArrayEquals(intArrayOf(1, 4, 2, 6), path)
    }

    @Test
    fun sample2() {
        val vertexNumber = 5
        val colors = intArrayOf(1, 2, 2, 2, 2)
        val targetColors = intArrayOf(2, 2, 2, 2, 1)
        val edges = arrayListOf(
            Pair(1, 2),
            Pair(2, 3),
            Pair(3, 4),
            Pair(4, 5)
        )
        val tree = Tree(vertexNumber, colors, targetColors, edges)
        val path = tree.findKingPath()!!.toIntArray()
        assertArrayEquals(intArrayOf(1, 2, 3, 4, 5), path)
    }

    @Test
    fun sample3() {
        val vertexNumber = 4
        val colors = intArrayOf(10, 20, 10, 20)
        val targetColors = intArrayOf(20, 10, 20, 10)
        val edges = arrayListOf(
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4)
        )
        val tree = Tree(vertexNumber, colors, targetColors, edges)
        assertNull(tree.findKingPath())
    }

    @Test
    fun sample4() {
        val vertexNumber = 2
        val colors = intArrayOf(1_000_000, 1_000_000)
        val targetColors = intArrayOf(1_000_000, 1_000_000)
        val edges = arrayListOf(
            Pair(1, 2)
        )
        val tree = Tree(vertexNumber, colors, targetColors, edges)
        val path = tree.findKingPath()!!.toIntArray()
        assertArrayEquals(IntArray(0), path)
    }

    @Test
    fun sample5() {
        val vertexNumber = 10
        val colors = intArrayOf(4, 2, 2, 4, 2, 4, 1, 2, 3, 4)
        val targetColors = intArrayOf(4, 2, 4, 4, 3, 2, 1, 2, 4, 2)
        val edges = arrayListOf(
            Pair(5, 8),
            Pair(6, 9),
            Pair(10, 5),
            Pair(1, 10),
            Pair(7, 10),
            Pair(3, 4),
            Pair(5, 9),
            Pair(3, 10),
            Pair(2, 4)
        )
        val tree = Tree(vertexNumber, colors, targetColors, edges)
        val path = tree.findKingPath()!!.toIntArray()
        assertArrayEquals(intArrayOf(3, 10, 5, 9, 6), path)
    }
}
