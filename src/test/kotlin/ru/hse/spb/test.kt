package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.*


class TestSource {

    private val pdfDir = "pdf"

    @Test
    fun testDocumentAndPreamble() {
        val document = document {
            documentclass("beamer")
            usepackage("color")
            usepackage("babel", "russian")
        }
        val expectedTex = """
            |\documentclass{beamer}
            |\usepackage{color}
            |\usepackage[russian]{babel}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()

        assertEquals(expectedTex, document.toString())
    }

    @Test
    fun testItemize() {
        val document = document {
            itemize {
                +"itemize"
                item {
                    +"item 1"
                }
                item {
                    +"item 2"
                }
            }
        }
        val expectedTex = """
            |\begin{document}
            |  \begin{itemize}
            |    itemize
            |    \item
            |      item 1
            |    \item
            |      item 2
            |  \end{itemize}
            |\end{document}
            |
        """.trimMargin()

        assertEquals(expectedTex, document.toString())
    }

    @Test
    fun testFrames() {
        val document = document {
            frame("first") {}
            frame {}
        }
        val expectedTex = """
            |\begin{document}
            |  \begin{frame}
            |    \frametitle{first}
            |  \end{frame}
            |  \begin{frame}
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin()

        assertEquals(expectedTex, document.toString())
    }

    private fun genPdf(source: String, name: String) {
        File("$pdfDir/$name.tex").writeText(source)
        val proc = Runtime.getRuntime().exec("pdflatex -output-directory=$pdfDir -shell-escape $pdfDir/$name.tex")
        val sc = Scanner(proc.inputStream)
        while (sc.hasNext()) {
            println(sc.nextLine())
        }
        sc.close()
        Runtime.getRuntime().exec("gio open $pdfDir/$name.pdf")
        listOf("$pdfDir/$name.aux", "$pdfDir/$name.log", "$pdfDir/$name.nav", "$pdfDir/$name.out", "$pdfDir/$name.snm", "$pdfDir/$name.toc").forEach {File(it).delete()}
    }

    @Test
    fun bigPdfTest() {
        val document = document {
            documentclass("beamer")
            frame {
                customMacro("title", "Kotlin") {}
                customMacro("author", "Magic") {}
                customMacro("maketitle") {}
            }
            frame("First slide") {
                alignment("center") {
                    +"WOW\\\\TEX\\\\IS\\\\SO\\\\COOL"
                }
                itemize {
                    item {
                        +"kotlin"
                        enumerate {
                            for (i in 1..5) {
                                item {
                                    +"kotlin \\#$i"
                                }
                            }
                        }
                    }
                    item {
                        +"kokotlin"
                    }
                    item {
                        +"kokokotlin"
                    }
                }
            }
            frame("Second slide") {
                +"Some math stuff\\\\"
                math {
                    +"f(x, y) = y + \\cosh \\sqrt[3]{x^2 + y^2}"
                    +"\\frac{\\partial f}{\\partial x} = sh \\sqrt[3]{x^2 + y^2} \\cdot \\frac{1}{3} (x^2 + y^2)^{-\\frac{2}{3}} \\cdot 2x"
                    +"\\frac{\\partial f}{\\partial y} = 1 + sh \\sqrt[3]{x^2 + y^2} \\cdot \\frac{1}{3} (x^2 + y^2)^{-\\frac{2}{3}} \\cdot 2y"
                }
            }
            frame {
                customTag("theorem") {
                    math {
                        +"a^2 + b^2 = c^2"
                    }
                }
            }
        }
        genPdf(document.toString(), "kek")
    }
}