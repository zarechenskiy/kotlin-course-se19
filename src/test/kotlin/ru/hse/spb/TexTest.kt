package ru.hse.spb

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.StringBuilder

class TexTest {
    private var builder = StringBuilder()

    @Before
    fun setUp() {
        builder = StringBuilder()
    }

    @Test
    fun testEmptyDocumentTag() {
        document {
        }.render(builder)
        assertEquals(
                """\begin{document}
                    |\end{document}
                    |
                """.trimMargin(),
                builder.toString()
        )
    }

    @Test
    fun testSingleTags() {
        document {
            userPackage("name", "arg1", "arg2")
            documentClass("name")
        }.render(builder)
        assertEquals(
                """\begin{document}
                    |\userpackage{name}[arg1,arg2]
                    |\documentclass{name}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString()
        )
    }

    @Test
    fun testMath() {
        document {
                math {
                    +"str1"
                    +"str2"
                }
            }.render(builder)
        assertEquals(
                """\begin{document}
                    |\begin{math}
                    |str1
                    |str2
                    |\end{math}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString())
    }

    @Test
    fun testFrame() {
        document {
                frame("title") {
                }
        }.render(builder)
        assertEquals(
                """\begin{document}
                    |\begin{frame}
                    |\frametitle{title}
                    |\end{frame}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString())
    }

    @Test
    fun testAlign() {
        document {
                alignment {
                    +"str1"
                    +"str2"
                }
            }.render(builder)
        assertEquals(
                """\begin{document}
                    |\begin{align}
                    |str1
                    |str2
                    |\end{align}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString())
    }

    @Test
    fun testEnumerate() {
        document {
                enumerate {
                    item {
                        +"str1"
                    }
                    item {
                        +"str2"
                    }
                    enumerate {
                        item {
                            +"str3"
                        }
                    }
                }
        }.render(builder)
        assertEquals(
                """\begin{document}
                    |\begin{enumerate}
                    |\item
                    |str1
                    |\item
                    |str2
                    |\begin{enumerate}
                    |\item
                    |str3
                    |\end{enumerate}
                    |\end{enumerate}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString())
    }

    @Test
    fun testItemize() {
        document {
                itemize {
                    item {
                        +"str1"
                    }
                    item {
                        +"str2"
                    }
                }
        }.render(builder)
        assertEquals(
                """\begin{document}
                    |\begin{itemize}
                    |\item
                    |str1
                    |\item
                    |str2
                    |\end{itemize}
                    |\end{document}
                    |
                """.trimMargin(), builder.toString())
    }

    @Test
    fun testSmoke() {
        document {
            documentClass("beamer")
            userPackage("babel", "russian")
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in listOf("1", "2")) {
                        item { +"$row text" }
                    }
                }
                customWrappingTag("pyglist", "language" to "kotlin") {
                    +"    val a = 1"
                }
            }
        }.render(builder)

        assertEquals(
                """
            \begin{document}
            \documentclass{beamer}
            \userpackage{babel}[russian]
            \begin{frame}[arg1=arg2]
            \frametitle{frametitle}
            \begin{itemize}
            \item
            1 text
            \item
            2 text
            \end{itemize}
            \begin{pyglist}[language=kotlin]
                val a = 1
            \end{pyglist}
            \end{frame}
            \end{document}

            """.trimIndent(),

                builder.toString()
                )
    }
}