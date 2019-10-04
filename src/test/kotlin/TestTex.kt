package ru.hse.spb

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestTex {

    @Test
    fun example() {
        val rows = arrayOf("1", "2", "3")
        assertEquals(
            """ |\documentclass{beamer}
                |\usepackage{babel, russian}
                |\begin{document}
                |  \begin{frame}[arg1=arg2]
                |    \frametitle{frametitle}
                |    \begin{itemize}
                |      \item
                |          1 text
                |      \item
                |          2 text
                |      \item
                |          3 text
                |    \end{itemize}
                |    \begin{pyglist}[language=kotlin]
                |      val a = 1
                |      
                |    \end{pyglist}
                |  \end{frame}
                |\end{document}
                |""".trimMargin(),
            document {
                documentClass("beamer")
                usepackage("babel", "russian" /* varargs */)
                frame("arg1" to "arg2", frameTitle = "frametitle") {
                    itemize {
                        for (row in rows) {
                            item { +"$row text" }
                        }
                    }

                    // begin{pyglist}[language=kotlin]...\end{pyglist}
                    customTag("language" to "kotlin", name = "pyglist") {
                        +"""
               |val a = 1
               |
            """.trimMargin()
                    }
                }
            }.toString()
        )
    }

    @Test
    fun testMath() {
        assertEquals(
            """|\begin{document}
               |  \begin{math}
               |    Hello there, I am an formula \int\limits_{x=-\infty}^{\infty} x\\
               |    kek
               |  \end{math}
               |\end{document}
               |""".trimMargin(),
            document {
                math {
                    +"Hello there, I am an formula \\int\\limits_{x=-\\infty}^{\\infty} x\\\\\nkek"
                }
            }.toString()
        )
    }

    @Test
    fun testEnumerate() {
        assertEquals(
            """|\begin{document}
               |  \begin{enumerate}
               |    \item
               |        a
               |    b
               |    \item
               |        c
               |  \end{enumerate}
               |\end{document}
               |""".trimMargin(),
            document {
                enumerate {
                    item { +"a" }
                    +"b"
                    item { +"c" }
                }
            }.toString()
        )
    }
}