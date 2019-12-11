package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.lang.IllegalStateException

class LatexTest {
    @Test
    fun testSimple() {
        val rows = arrayOf("first", "second")
        val actual = document {
            documentClass("beamer")
            usePackage("babel", "russian")
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
                    }
                }

                customTag("pyglist", "language" to "kotlin") {
                    +"""
                       |val a = 1
                    """.trimMargin()
                }
            }
        }.toString()

        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \begin{frame}[arg1=arg2]
            |    \begin{itemize}
            |      \begin{item}
            |        first text
            |      \end{item}
            |      \begin{item}
            |        second text
            |      \end{item}
            |    \end{itemize}
            |    \begin{pyglist}[language=kotlin]
            |      val a = 1
            |    \end{pyglist}
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testMultipleParameters() {
        val actual = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            frame("frametitle", "arg1" to "arg2", "arg3" to "arg4") {
            }
        }.toString()

        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian, english]{babel}
            |\begin{document}
            |  \begin{frame}[arg1=arg2, arg3=arg4]
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testAllEnvironments() {
        val actual = document {
            documentClass("article")
            frame("Test frame") {
                math {
                    +"1 + 2 = 3\\\\"
                    +"x^2 = y"
                }
            }
            itemize {
                item {
                    align {
                        +"1\\\\"
                        +"2\\\\"
                    }
                }
                item {
                    enumerate {
                        item {
                            +"first"
                        }
                        item {
                            +"second"
                        }
                    }
                }
            }
        }.toString()

        val expected = """
            |\documentclass{article}
            |\begin{document}
            |  \begin{frame}
            |    \begin{math}
            |      1 + 2 = 3\\
            |      x^2 = y
            |    \end{math}
            |  \end{frame}
            |  \begin{itemize}
            |    \begin{item}
            |      \begin{align}
            |        1\\
            |        2\\
            |      \end{align}
            |    \end{item}
            |    \begin{item}
            |      \begin{enumerate}
            |        \begin{item}
            |          first
            |        \end{item}
            |        \begin{item}
            |          second
            |        \end{item}
            |      \end{enumerate}
            |    \end{item}
            |  \end{itemize}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testEmptyDocument() {
        val actual = document {
            documentClass("beamer")
            usePackage("babel", "russian" )
            usePackage("tikz")
        }.toString()

        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\usepackage{tikz}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testOutputStream() {
        val stream = ByteArrayOutputStream()
        document {
            documentClass("article")
            itemize {
                item { +"item" }
            }
        }.toOutputStream(stream)

        val expected = """
            |\documentclass{article}
            |\begin{document}
            |  \begin{itemize}
            |    \begin{item}
            |      item
            |    \end{item}
            |  \end{itemize}
            |\end{document}
            |
        """.trimMargin()

        assertEquals(expected, stream.toString())
    }

    @Test(expected = IllegalStateException::class)
    fun testNoDocumentClass() {
        document {
            itemize {
                item { +"item" }
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testMultipleDocumentClass() {
        document {
            documentClass("beamer")
            documentClass("article")
            itemize {
                item { +"item" }
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testUsePackageBeforeDocumentClass() {
        document {
            usePackage("tikz")
            documentClass("article")
            itemize {
                item { +"item" }
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testUsePackageNotInPreamble() {
        document {
            documentClass("article")
            itemize {
                item { +"item" }
            }
            usePackage("tikz")
        }
    }
}