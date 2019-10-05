package ru.spb.hse

import org.junit.Assert.assertEquals
import org.junit.Test

internal class TeXBuilderTest {
    @Test
    fun onlyDocumentClassTest() {
        val text = document {
            documentClass("aa")
        }
        val expected = """
            |\documentclass{aa}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }

    @Test
    fun documentClassWithParametersTest() {
        val text = document {
            documentClass("aa", "kop", "lopl")
        }
        val expected = """
            |\documentclass[kop,lopl]{aa}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }

    @Test
    fun usePackageTest() {
        val text = document {
            documentClass("aa")
            usepackage("mi", "pp", "oo")
            usepackage("lp")
        }
        val expected = """
            |\documentclass{aa}
            |\usepackage[pp,oo]{mi}
            |\usepackage{lp}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }

    @Test
    fun sampleTest() {
        val text = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in arrayOf(2, 3, 9)) {
                        item { + "$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("pyglist", "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """.trimMargin()
                }
            }
        }
        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |    \begin[arg1=arg2]{frame}
            |        \frametitle{frametitle}
            |        \begin{itemize}
            |            \item
            |                2 text
            |            \item
            |                3 text
            |            \item
            |                9 text
            |        \end{itemize}
            |        \begin[language=kotlin]{pyglist}
            |            val a = 1
            |
            |        \end{pyglist}
            |    \end{frame}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }

    @Test
    fun nestedItemizeTest() {
        val text = document {
            documentClass("aa", "kop", "lopl")
            itemize {
                item {
                    itemize {
                        item {
                            +"here"
                        }
                    }
                }
            }
        }
        val expected = """
            |\documentclass[kop,lopl]{aa}
            |\begin{document}
            |    \begin{itemize}
            |        \item
            |            \begin{itemize}
            |                \item
            |                    here
            |            \end{itemize}
            |    \end{itemize}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }

    @Test
    fun differentTagsTest() {
        val text = document {
            documentClass("aa", "kop", "lopl")
            usepackage("42")
            frame("kokoko") {
                +"111"
            }
            math {
                +"lsk"
            }
            customTag("424242", "88" to "00") {

            }
            alignment {
                +"90898"
                +"98903u9"
            }
        }
        val expected = """
            |\documentclass[kop,lopl]{aa}
            |\usepackage{42}
            |\begin{document}
            |    \begin{frame}
            |        \frametitle{kokoko}
            |        111
            |    \end{frame}
            |    \begin{math}
            |        lsk
            |    \end{math}
            |    \begin[88=00]{424242}
            |    \end{424242}
            |    \begin{align*}
            |        90898
            |        98903u9
            |    \end{align*}
            |\end{document}
            |
        """.trimMargin()
        assertEquals(expected, text.toString())
    }
}