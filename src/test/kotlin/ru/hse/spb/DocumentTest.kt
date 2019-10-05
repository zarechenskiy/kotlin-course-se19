package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class DSLTeXTest {

    @Test
    fun exampleTest() {
        val rows = listOf("qwe", "rty", "uio")

        val tex = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("arg1" to "arg2", frameTitle = "frameTitle") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
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

        val expectedTex = """
            |\begin{document}
            |  \documentClass{beamer}
            |  \usepackage[russian]{babel}
            |  \begin{frame}[arg1=arg2]
            |    \frametitle{frameTitle}
            |    \begin{itemize}
            |      \item
            |        qwe text
            |      \item
            |        rty text
            |      \item
            |        uio text
            |    \end{itemize}
            |    \begin{pyglist}[language=kotlin]
            |      val a = 1
            |
            |    \end{pyglist}
            |  \end{frame}
            |\end{document}
            |""".trimMargin()
        assertEquals(expectedTex, tex)
    }

    @Test
    fun testCommand() {
        val tex = document {
            documentClass("beamer")
            usepackage("babel", "russian", "english", "spanish")
        }.toString()

        val expected = """
            |\begin{document}
            |  \documentClass{beamer}
            |  \usepackage[russian, english, spanish]{babel}
            |\end{document}
            |""".trimMargin()

        assertEquals(expected, tex)
    }

    @Test
    fun testTag() {
        val tex = document {
            frame("1" to "2", "3" to "4", frameTitle = "title") {
                itemize {
                    item {
                        +"1"
                    }
                    item {
                        +"2"
                    }
                }
                enumerate {
                    item {
                        +"1"
                    }
                    item {
                        +"2"
                    }
                }
                math {
                    +"1=2"
                }
                flushleft {
                    +"sample"
                }
                flushright {
                    +"sample"
                }
                center {
                    +"sample"
                }
            }
        }.toString()

        val expected = """
            |\begin{document}
            |  \begin{frame}[1=2, 3=4]
            |    \frametitle{title}
            |    \begin{itemize}
            |      \item
            |        1
            |      \item
            |        2
            |    \end{itemize}
            |    \begin{enumerate}
            |      \item
            |        1
            |      \item
            |        2
            |    \end{enumerate}
            |    \begin{gather*}
            |      1=2
            |    \end{gather*}
            |    \begin{flushleft}
            |      sample
            |    \end{flushleft}
            |    \begin{flushright}
            |      sample
            |    \end{flushright}
            |    \begin{center}
            |      sample
            |    \end{center}
            |  \end{frame}
            |\end{document}
            |""".trimMargin()

        assertEquals(expected, tex)
    }

    @Test
    fun testOutputStream() {
        val stream = ByteArrayOutputStream()
        document {}.toOutputStream(stream)
        val expected = """
            |\begin{document}
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, stream.toString())
    }
}