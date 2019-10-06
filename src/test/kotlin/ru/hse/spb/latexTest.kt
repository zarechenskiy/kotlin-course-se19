package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

val withProperties = """
\documentclass{beamer}
\usepackage{babel}
\usepackage{russian}
\begin{document}
\begin{frame}\frametitle{Title}

\end{frame}
\end{document}
""".trimIndent()

val itemize = """
\begin{itemize}
    \item First
    \item Second
    \item Third
\end{itemize}
""".trimIndent()

val withArgument = """
\begin{document}
\begin{frame}[arg1=arg2]\frametitle{frametitle}

\end{frame}
\end{document}
""".trimIndent()

val withFormula = """
\begin{document}
\begin{frame}\frametitle{Title}
${'$'}x = 5${'$'}
\end{frame}
\end{document}
""".trimIndent()

val align = """
\begin{align}
    1 + 2 &= 3 \\
    567 + 1 &= 568
\end{align}"""

val example = """
\documentclass{beamer}
\usepackage{babel}
\usepackage{russian}
\begin{document}
\begin{frame}[arg1=arg2]\frametitle{frametitle}
\begin{itemize}
    \item 1 text
    \item 2 text
    \item 3 text
\end{itemize}
\begin{pyglist}[language=kotlin]

               |val a = 1
               |
            
\end{pyglist}
\end{frame}
\end{document}
""".trimIndent()

class TestLatex {
    @Test
    fun testProperties() {
        val file = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("Title") {

            }
        }
        assertEquals(withProperties, file.toString())
    }

    @Test
    fun testItemize() {
        val file = document {
            frame("Title") {
                itemize {
                    item {
                        +"First"
                    }
                    item {
                        +"Second"
                    }
                    item {
                        +"Third"
                    }
                }
            }
        }
        assertTrue(file.toString().contains(itemize))
    }

    @Test
    fun testParameter() {
        val file = document {
            frame(frameTitle = "frametitle", param = "arg1" to "arg2") {

            }

        }
        assertEquals(withArgument, file.toString())
    }

    @Test
    fun testMath() {
        val file = document {
            frame("Title") {
                math {
                    +"x = 5"
                }
            }
        }
        assertEquals(withFormula, file.toString())
    }

    @Test
    fun testAlign() {
        val file = document {
            frame("Title") {
                align {
                    line("1 + 2 ", "= 3")
                    line("567 + 1 ", "= 568")
                }
            }
        }
        assertTrue(file.toString().contains(align))

    }

    @Test
    fun testExample() {
        val file = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame(frameTitle="frametitle", param = "arg1" to "arg2") {
                itemize {
                    for (row in 1..3) {
                        item { + "$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag(name = "pyglist", param = "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """
                }
            }
        }
        assertEquals(example, file.toString())
    }
}