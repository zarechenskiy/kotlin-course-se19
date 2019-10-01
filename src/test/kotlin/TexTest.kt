package ru.hse.dsl

import org.junit.Assert
import org.junit.Test
import java.lang.StringBuilder

class TexTest {

    @Test
    fun creatingFramesTest() {
        val stringBuilder = StringBuilder()
        tex {
            documentclass("beamer")
            usepackage("fontenc", "T2A", "T1")
            usepackage( "inputenc", "utf8")
            usepackage("babel", "russian", "english")
            document {
                frame {
                    itemize {
                        val items = listOf(1, 2, 3, 4)
                        for (i in items) {
                            item {
                                +"$i text"
                            }
                        }
                    }
                }
                frame("F") {
                    +"1 + 2"
                    math {
                        +"1 + 2"
                    }
                }
                frame("F", "a" to "b", "c" to "d") {
                    customTag("pyglist", "language" to "kotlin") {
                        +"""
                       |val a = 1
                    """.trimMargin()
                    }
                }
            }
        }.render(stringBuilder)
        Assert.assertEquals(stringBuilder.toString(), """
                |\documentclass[]{beamer}
                |\usepackage[T2A,T1]{fontenc}
                |\usepackage[utf8]{inputenc}
                |\usepackage[russian,english]{babel}
                |
                |
                |\begin{document}
                |
                |\begin{frame}[]
                |
                |\begin{itemize}
                |    \item 1 text
                |    \item 2 text
                |    \item 3 text
                |    \item 4 text
                |\end{itemize}
                |
                |\end{frame}
                |
                |
                |\begin{frame}{F}[]
                |1 + 2
                |
                |\begin{math}
                |1 + 2
                |\end{math}
                |
                |\end{frame}
                |
                |
                |\begin{frame}{F}[a=b,c=d]
                |
                |\begin{pyglist}[language=kotlin]
                |val a = 1
                |\end{pyglist}
                |
                |\end{frame}
                |
                |\end{document}
                |
                |""".trimMargin()
        )
    }

    @Test
    fun innerTagsTest() {
        val stringBuilder = StringBuilder()
        tex {
            documentclass("article")
            usepackage("fontenc", "T2A", "T1")
            usepackage( "inputenc", "utf8")
            usepackage("babel", "russian", "english")
            document {
                alignment("flushright") {
                    enumerate {
                        item {
                            +"first"
                        }
                        item {
                            +"second"
                        }
                        item {
                            +"third"
                        }
                    }
                }
                customTag("my_perfect_tag", "planet" to "Earth") {
                    alignment("flushleft") {
                        +"This text must be on the left side"
                        math { 1 + 1 + 234}
                    }
                    alignment("centre") {
                        +"This text must be in the centre"
                    }
                }

            }
        }.render(stringBuilder)
        Assert.assertEquals(stringBuilder.toString(), """
                |\documentclass[]{article}
                |\usepackage[T2A,T1]{fontenc}
                |\usepackage[utf8]{inputenc}
                |\usepackage[russian,english]{babel}
                |
                |
                |\begin{document}
                |
                |\begin{flushright}
                |
                |\begin{enumerate}
                |    \item first
                |    \item second
                |    \item third
                |\end{enumerate}
                |
                |\end{flushright}
                |
                |
                |\begin{my_perfect_tag}[planet=Earth]
                |
                |\begin{flushleft}
                |This text must be on the left side
                |
                |\begin{math}
                |\end{math}
                |
                |\end{flushleft}
                |
                |
                |\begin{centre}
                |This text must be in the centre
                |\end{centre}
                |
                |\end{my_perfect_tag}
                |
                |\end{document}
                |
                |""".trimMargin()
        )
    }

    @Test
    fun toOutputStreamTest() {
        tex {
            documentclass("beamer")
            usepackage("fontenc", "T2A", "T1")
            usepackage( "inputenc", "utf8")
            usepackage("babel", "russian", "english")
            document {
                frame("Math") {
                    math {
                        +"1 * 3 = 3 "
                    }
                }
                frame("Russian") {
                    +"Привет, TEX!"
                }
                frame("Algorithms") {
                    enumerate {
                        item {
                            +"Quick sort"
                        }
                        item {
                            +"Binary search"
                        }
                    }
                }
            }
        }.toOutputStream(System.out)
    }

}