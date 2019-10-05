package ru.spb.hse.texDSL

import org.junit.Assert.assertEquals
import org.junit.Test

class TexBuilderTest {
    @Test
    fun testEmpty() {
        assertEquals(
            """
            \begin{document}
            \end{document}
            """.trimIndent(),

            document {
            }.render()
        )
    }

    @Test
    fun testHeaders() {
        assertEquals(
            """
            \documentclass{beamer}
            \usepackage[russian]{babel}
            \usepackage{makeidx}
            \begin{document}
            \end{document}
            """.trimIndent(),

            document {
                documentClass("beamer")
                usePackage("babel", "russian")
                usePackage("makeidx")
            }.render()
        )
    }

    @Test
    fun testLists() {
        assertEquals(
            """
            \begin{document}
            \begin{enumerate}
            \item 1
            \item 2
            \item 
            \end{enumerate}
            \begin{itemize}
            \item a
            \item b
            c
            \end{itemize}
            \end{document}
            """.trimIndent(),

            document {
                enumerate {
                    item {
                        + "1"
                    }
                    item {
                        + "2"
                    }
                    item {

                    }
                }
                itemize {
                    item {
                        + "a"
                    }
                    item {
                        + "b"
                        + "c"
                    }
                }
            }.render()
        )
    }

    @Test
    fun testFrame() {
        assertEquals(
            """
            \begin{document}
            \begin{frame}
            \frametitle{title}
            \begin{center}
            text1
            \end{center}
            \begin{flushleft}
            text2
            \end{flushleft}
            \begin{flushright}
            text3
            \end{flushright}
            \end{frame}
            \end{document}
            """.trimIndent(),

            document {
                frame("title") {
                    center {
                        + "text1"
                    }
                    flushLeft {
                        + "text2"
                    }
                    flushRight {
                        + "text3"
                    }
                }
            }.render()
        )
    }

    @Test
    fun testMath() {
        assertEquals(
            """
            \begin{document}
            \begin{math}
            (a + b)^2 = a^2 + 2ab + b^2
            \end{math}
            \end{document}
            """.trimIndent(),

            document {
                math {
                    +"(a + b)^2 = a^2 + 2ab + b^2"
                }
            }.render()
        )
    }

    @Test
    fun testFromExample() {
        assertEquals(
            """
            \documentclass{beamer}
            \usepackage[russian]{babel}
            \begin{document}
            \begin{frame}[arg1=arg2]
            \frametitle{frametitle}
            \begin{itemize}
            \item 1 text
            \item 2 text
            \end{itemize}
            \begin{pyglist}[language=kotlin]
                val a = 1
            \end{pyglist}
            \end{frame}
            \end{document}
            """.trimIndent(),

            document {
                documentClass("beamer")
                usePackage("babel", "russian")
                frame("frametitle", "arg1" to "arg2") {
                    itemize {
                        for (row in listOf("1", "2")) {
                            item { + "$row text" }
                        }
                    }
                    customTag("pyglist", "language" to "kotlin") {
                        +"    val a = 1"
                    }
                }
            }.render()
        )
    }

}