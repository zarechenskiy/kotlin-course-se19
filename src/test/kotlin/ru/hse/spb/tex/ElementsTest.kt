package ru.hse.spb.tex

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.Writer

internal class ElementsTest {

    lateinit var subject: Elements

    @BeforeEach
    fun init() {
        subject = Elements()
    }

    @Test
    fun adds() {
        val testString = "test"
        subject.addElement(TextStatement(testString))
        assertEquals(testString + "\n", subject.stringRepresentation)
    }

    @Test
    fun `adds multiple times`() {
        val testString1 = "test1"
        val testString2 = "test2"
        subject.addElement(TextStatement(testString1))
        subject.addElement(TextStatement(testString2))
        assertEquals("$testString1\n$testString2\n", subject.stringRepresentation)
    }

    @Test
    fun `addElement returns`() {
        val statement = TextStatement("")
        assertSame(statement, subject.addElement(statement))
    }

    class ModifiableTextStatement(var text: String) : Element {
        override fun render(output: Writer, indent: String) {
            output.append(text)
        }
    }

    @Test
    fun `adds exactly the statement I want`() {
        val mStatement = ModifiableTextStatement("")
        subject.addElement(mStatement)

        val testString = "ewq"
        mStatement.text = testString
        assertEquals(testString, subject.stringRepresentation)
    }

    @Test
    fun `runs init`() {
        val mStatement = ModifiableTextStatement("!")
        val testString = "(||||:)"
        subject.addElement(mStatement) {
            text += testString
        }
        assertEquals("!" + testString, subject.stringRepresentation)
    }


}