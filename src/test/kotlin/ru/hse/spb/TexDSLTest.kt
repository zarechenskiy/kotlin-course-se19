package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream

class TexDSLTest {
    @Test
    fun testPreamble() {
        val doc = document {
            documentClass("beamer")
            usePackage("babel", "russian", "xcolor")
        }.toString()
        val exp = """\documentclass{beamer}
           |\usepackage{babel}
           |\usepackage{russian}
           |\usepackage{xcolor}
           """.trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testSimple() {
        val doc = document {
            documentClass("beamer")
            usePackage("babel", "russian")

            frame("title") {
                center {
                    +"Some text in the center of the screen"
                }
            }
        }.toString()
        val exp = """\documentclass{beamer}
           |\usepackage{babel}
           |\usepackage{russian}
           |\begin{document}
           |\begin{frame}
           |\frametitle{title}
           |\begin{center}
           |Some text in the center of the screen
           |\end{center}
           |\end{frame}
           |\end{document}""".trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testSimpler() {
        val doc = document {
            documentClass("beamer")
            frame("Simple") {
                +"Text in the frame"
            }
        }.toString()
        val exp = """\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\frametitle{Simple}
            |Text in the frame
            |\end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testMath() {
        val doc = document {
            documentClass("beamer")
            frame("Simple") {
                math("""\forall a \in N, a < k""")
            }
        }.toString()
        val exp = """\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\frametitle{Simple}
            |\begin{math}
            |\forall a \in N, a < k
            |\end{math}
            |\end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(exp, doc)
    }


    @Test
    fun testList() {
        val doc = document {
            documentClass("beamer")
            usePackage("babel")
            frame("Itemize") {
                itemize {
                    for (row in listOf("1", "2", "3")) {
                        item { +"Item$row" }
                    }
                }
            }
            frame("Enumerate") {
                enumerate {
                    for (row in listOf("1", "2", "3")) {
                        item { +"Item$row" }
                    }
                }
            }
        }.toString()
        val exp = """\documentclass{beamer}
            |\usepackage{babel}
            |\begin{document}
            |\begin{frame}
            |\frametitle{Itemize}
            |\begin{itemize}
            |\item
            |Item1
            |\item
            |Item2
            |\item
            |Item3
            |\end{itemize}
            |\end{frame}
            |\begin{frame}
            |\frametitle{Enumerate}
            |\begin{enumerate}
            |\item
            |Item1
            |\item
            |Item2
            |\item
            |Item3
            |\end{enumerate}
            |\end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testAlignment() {
        val doc = document {
            documentClass("beamer")
            frame("Alignment") {
                center {
                    +"Text in the center."
                }
                flushright {
                    +"Text on the right."
                }
            }
        }.toString()
        val exp = """\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\frametitle{Alignment}
            |\begin{center}
            |Text in the center.
            |\end{center}
            |\begin{flushright}
            |Text on the right.
            |\end{flushright}
            |\end{frame}
            |\end{document}""".trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testCustomTag() {
        val doc = document {
            documentClass("beamer")
            usePackage("babel", "russian")

            frame("Code Example", "arg1" to "arg2") {
                customTag("pyglist", "language" to "kotlin") {
                    +"""
                        |val a = 1
                        |
                    """
                }
            }
        }.toString()
        val exp = """\documentclass{beamer}
            |\usepackage{babel}
            |\usepackage{russian}
            |\begin{document}
            |\begin{frame}[arg1=arg2]
            |\frametitle{Code Example}
            |\begin{pyglist}[language=kotlin]
            |val a = 1
            |
            |\end{pyglist}
            |\end{frame}
            |\end{document}""".trimMargin()
        assertEquals(exp, doc)
    }

    @Test
    fun testOutputStream() {
        val out = ByteArrayOutputStream()
        document {
            documentClass("beamer")
            usePackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in listOf("1", "2")) {
                        item { +"$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("pyglist", "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """
                }
            }
        }.toOutputStream(out)
        val exp = ("""\documentclass{beamer}\usepackage{babel}
                     |\usepackage{russian}\begin{document}\begin{frame}[arg1=arg2]\frametitle{frametitle}""" +
                """\begin{itemize}\item1 text\item2 text\end{itemize}\begin{pyglist}[language=kotlin]val a = 1
                     |\end{pyglist}\end{frame}\end{document}
                  """).trimMargin()
        assertEquals(exp, out.toString())
    }
}

