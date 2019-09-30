package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.texdsl.document

class TestSource {
    @Test
    fun testDocument() {
        val result = document {
            documentClass("article")
            + "my super text"
        }.toString()

        assertEquals("""
            |\documentclass{article}
            |
            |
            |\begin{document}
            |  my super text
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testUsePackage() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            usePackage("cmap")
            usePackage("fontec", "T2A")
            usePackage("tikz")
        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |\usepackage{cmap}
            |\usepackage[T2A]{fontec}
            |\usepackage{tikz}
            |
            |
            |\begin{document}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testFrame() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            frame(name = "Slide 1") {
                + "info 1.1\\\\"
                + "info 1.2\\\\"
            }

            frame("Slide 2", "arg1" to "arg2") {
                + "info 2.1\\\\"
                + "info 2.2\\\\"
            }
        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{frame}
            |    \frametitle{Slide 1}
            |    info 1.1\\
            |    info 1.2\\
            |  \end{frame}
            |  \begin{frame}[arg1=arg2]
            |    \frametitle{Slide 2}
            |    info 2.1\\
            |    info 2.2\\
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testEnumerate() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            enumerate {
                item {
                    + "First\\\\"
                }
                item {
                    + "Second\\\\"
                }
                item {
                    + "Third"
                    enumerate {
                        item { + "3.1" }
                        item { + "3.2" }
                    }
                }
            }
        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{enumerate}
            |    \item
            |      First\\
            |    \item
            |      Second\\
            |    \item
            |      Third
            |      \begin{enumerate}
            |        \item
            |          3.1
            |        \item
            |          3.2
            |      \end{enumerate}
            |  \end{enumerate}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testItemize() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            itemize {
                item {
                    + "An element\\\\"
                }
                item {
                    + "An element\\\\"
                }
                item {
                    + "An element"
                    itemize {
                        item { + "An element" }
                        item { + "An element" }
                    }
                }
            }
        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{itemize}
            |    \item
            |      An element\\
            |    \item
            |      An element\\
            |    \item
            |      An element
            |      \begin{itemize}
            |        \item
            |          An element
            |        \item
            |          An element
            |      \end{itemize}
            |  \end{itemize}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testMath() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            math {
                + """\frac{x}{y} + c = 6"""
            }

        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{math}
            |    \frac{x}{y} + c = 6
            |  \end{math}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testCustomTag() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            customTag("mytag", "parameter" to "value") {
                + "text"
            }

        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{mytag}[parameter=value]
            |    text
            |  \end{mytag}
            |\end{document}
            |
        """.trimMargin(), result)
    }

    @Test
    fun testAllignment() {
        val result = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            flushLeft {
                + "left text"
            }
            flushRight {
                + "right text"
            }
            center {
                + "center text"
            }

        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian,english]{babel}
            |
            |
            |\begin{document}
            |  \begin{flushleft}
            |    left text
            |  \end{flushleft}
            |  \begin{flushright}
            |    right text
            |  \end{flushright}
            |  \begin{center}
            |    center text
            |  \end{center}
            |\end{document}
            |
        """.trimMargin(), result)
    }
}