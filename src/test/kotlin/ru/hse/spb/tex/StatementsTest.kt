package ru.hse.spb.tex

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StatementsTest {

    lateinit var subject: Statements

    fun test(expected: String, init: Statements.() -> Unit) {
        test(expected, subject, init)
    }

    @BeforeEach
    fun init() {
        subject = Statements()
    }

    @Test
    fun unaryPlusTest() {
        test("""
            asd
            dsa
            
        """.trimIndent()) {
            +"asd"
            +"dsa"
        }
    }

    @Test
    fun itemTagTest() {
        test("""
            \begin{kek}
            \end{kek}
            
        """.trimIndent()) {
            itemTag("kek")
        }
    }

    @Test
    fun `itemTag with no items`() {
        test("""
            \begin{kek}
            \end{kek}
            
        """.trimIndent()) {
            itemTag("kek")
        }
    }

    @Test
    fun `itemTag with items`() {
        test("""
            \begin{kek}
                \item
                \item
            \end{kek}
            
        """.trimIndent()) {
            itemTag("kek") {
                item
                item
            }
        }
    }

    @Test
    fun `enumerate with items`() {
        test("""
            \begin{enumerate}
                \item
                \item
            \end{enumerate}
            
        """.trimIndent()) {
            enumerate {
                item
                item
            }
        }
    }

    @Test
    fun `enumerate with no items`() {
        test("""
            \begin{enumerate}
            \end{enumerate}
            
        """.trimIndent()) {
            enumerate
        }
    }

    @Test
    fun `itemize with items`() {
        test("""
            \begin{itemize}
                \item
                \item
            \end{itemize}
            
        """.trimIndent()) {
            itemize {
                item
                item
            }
        }
    }

    @Test
    fun emptyLn() {
        test("\n") {
            emptyLn()
        }
    }

    @Test
    fun `empty custom tag`() {
        test("""
            \begin{kek}
            \end{kek}
            
        """.trimIndent()) {
            customTag("kek")
        }
    }

    @Test
    fun `custom tag with body`() {
        test("""
            \begin{kek}
            \end{kek}
            
        """.trimIndent()) {
            customTag("kek") {
                assertEquals(Statements::class, this::class)
            }
        }
    }

    @Test
    fun `empty customManualNewlineTag test`() {
        test("""
            \begin{kek}
            \end{kek}
            
        """.trimIndent()) {
            customManualNewlineTag("kek")
        }
    }

    @Test
    fun `customManualNewlineTag test`() {
        test("""
            \begin{kek}
                asdad\\
                bob\\
            \end{kek}
            
        """.trimIndent()) {
            customManualNewlineTag("kek") {
                +"asdad"
                +"bob"
            }
        }
    }

    @Test
    fun `math test`() {
        test("""
            \begin{math}
                asdad\\
                bob\\
            \end{math}
            
        """.trimIndent()) {
            math {
                +"asdad"
                +"bob"
            }
        }
    }

    @Test
    fun `align test`() {
        test("""
            \begin{align*}
                asdad\\
                bob\\
            \end{align*}
            
        """.trimIndent()) {
            align {
                +"asdad"
                +"bob"
            }
        }
    }

    @Test
    fun `basic command test`() {
        val command = "BOB"
        test("\\$command\n") {
            command(command)
        }
    }

    @Test
    fun `command with arguments test`() {
        val command = "BOB"
        test("\\$command{a1, a2, a3}\n") {
            command(command)("a1", "a2", "a3")
        }
    }

    @Test
    fun `command with square brackets test`() {
        val command = "BOB"
        test("\\$command[a1, a2, a3]\n") {
            command(command)["a1", "a2", "a3"]
        }
    }

    @Test
    fun `command with multiple arguments test`() {
        val command = "BOB"
        test("\\$command[a1]{a2}[a3]{a4}\n") {
            command(command)["a1"]("a2")["a3"]("a4")
        }
    }

    @Test
    fun `command with multiple empty arguments test`() {
        val command = "BOB"
        test("\\$command[]{}[][]{}{}\n") {
            command(command)[""]()[""][""]()()
        }
    }

    @Test
    fun `bracesCommand test`() {
        val command = "qweewq"
        test("""
            \$command{
                asdad
                bob
            }
            
        """.trimIndent()) {
            bracesCommand(command) {
                +"asdad"
                +"bob"
            }
        }
    }
}