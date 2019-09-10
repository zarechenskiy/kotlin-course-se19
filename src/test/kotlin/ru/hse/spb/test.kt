package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {

    @Test
    fun test51BSample1() {
        assertEquals("1 ", getAnswer("<table><tr><td></td></tr></table>"))
    }

    @Test
    fun test51BSample2() {
        assertEquals("1 4 ", getAnswer("""<table>
<tr>
<td>
<table><tr><td></td></tr><tr><td></
td
></tr><tr
><td></td></tr><tr><td></td></tr></table>
</td>
</tr>
</table>
"""))
    }

    @Test
    fun test51BSample3() {
        assertEquals("1 1 1 3 ", getAnswer("""<table><tr><td>
<table><tr><td>
<table><tr><td>
<table><tr><td></td><td></td>
</tr><tr><td></td></tr></table>
</td></tr></table>
</td></tr></table>
</td></tr></table>
"""))
    }

}