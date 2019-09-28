package ru.hse.lyubortk.kotlin

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.util.stream.Collectors.toList
import java.util.stream.IntStream

class TexDslTest {
    @Test
    fun testFromSample() {
        val rows = listOf("one", "two", "three")

        val texString = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("arg1" to "arg2", frameTitle = "frametitle") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
                    }
                }
                customTag("pyglist", "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """.trimMargin()
                }
            }
        }.toString()

        assertEquals("""
            |\documentclass{beamer}
            |\usepackage{babel, russian}
            |\begin{document}
            |    \begin{frame}[arg1=arg2]
            |        \frametitle{frametitle}
            |        \begin{itemize}
            |            \item
            |                one text
            |            \item
            |                two text
            |            \item
            |                three text
            |        \end{itemize}
            |        \begin{pyglist}[language=kotlin]
            |            val a = 1
            |
            |        \end{pyglist}
            |    \end{frame}
            |\end{document}
            |
        """.trimMargin(), texString)
    }

    @Test
    fun testAllEnvironments() {
        val texString = document {
            documentClass("beamer")
            usepackage("amsmath", "babel")
            frame("a" to "b", "c" to "d") {
                +"one"
            }
            frame("e" to "f", frameTitle = "title") {
                +"two"
            }
            itemize("a" to "b", "e" to "f") {
                item {
                    +"three"
                }
            }
            enumerate() {
                item {
                    +"four"
                }
                item {
                    +"five"
                    +"six"
                }
            }
            math {
                +"\\frac{1}{2} + 2"
            }
            flushleft {
                +"seven"
            }
            center {
                +"eight"
            }
            flushright {
                +"nine"
            }
        }.toString()
        assertEquals("""
            |\documentclass{beamer}
            |\usepackage{amsmath, babel}
            |\begin{document}
            |    \begin{frame}[a=b, c=d]
            |        one
            |    \end{frame}
            |    \begin{frame}[e=f]
            |        \frametitle{title}
            |        two
            |    \end{frame}
            |    \begin{itemize}[a=b, e=f]
            |        \item
            |            three
            |    \end{itemize}
            |    \begin{enumerate}
            |        \item
            |            four
            |        \item
            |            five
            |            six
            |    \end{enumerate}
            |    \begin{gather*}
            |        \frac{1}{2} + 2
            |    \end{gather*}
            |    \begin{flushleft}
            |        seven
            |    \end{flushleft}
            |    \begin{center}
            |        eight
            |    \end{center}
            |    \begin{flushright}
            |        nine
            |    \end{flushright}
            |\end{document}
            |
        """.trimMargin(), texString)
    }

    @Test
    fun testNestedEnvironments() {
        val lines = IntStream.range(2, 10).mapToObj {
            "I am line number $it"
        }.collect(toList())

        val texString = document {
            documentClass("article")
            usepackage("package")
            frame(frameTitle = "frameTitle") {
                itemize() {
                    item {
                    }
                    item {
                        enumerate() {
                            item {
                                +"Hello! I am the first item here!"
                            }
                            for (line in lines) {
                                item {
                                    +line
                                }
                            }

                        }
                    }
                }
            }
        }.toString()
        assertEquals("""
            |\documentclass{article}
            |\usepackage{package}
            |\begin{document}
            |    \begin{frame}
            |        \frametitle{frameTitle}
            |        \begin{itemize}
            |            \item
            |            \item
            |                \begin{enumerate}
            |                    \item
            |                        Hello! I am the first item here!
            |                    \item
            |                        I am line number 2
            |                    \item
            |                        I am line number 3
            |                    \item
            |                        I am line number 4
            |                    \item
            |                        I am line number 5
            |                    \item
            |                        I am line number 6
            |                    \item
            |                        I am line number 7
            |                    \item
            |                        I am line number 8
            |                    \item
            |                        I am line number 9
            |                \end{enumerate}
            |        \end{itemize}
            |    \end{frame}
            |\end{document}
            |
        """.trimMargin(), texString)
    }

    @Test
    fun testCannotSpecifyMultipleDocumentClasses() {
        assertThrows(RuntimeException::class.java) {
            document {
                documentClass("class1")
                documentClass("class2")
                enumerate() {
                    item {
                        +"hello there!"
                    }
                }
            }
        }
    }

    @Test
    fun testMustSpecifyDocumentClass() {
        assertThrows(RuntimeException::class.java) {
            document {
                enumerate() {
                    item {
                        +"hello there!"
                    }
                }
            }
        }
    }

    @Test
    fun testOutputToStream() {
        val outputStream = ByteOutputStream()
        outputStream.use {
            document {
                documentClass("class")
                math {
                    +"a + b"
                }
            }.toOutputStream(outputStream)
        }
        assertEquals("""
            |\documentclass{class}
            |\begin{document}
            |    \begin{gather*}
            |        a + b
            |    \end{gather*}
            |\end{document}
            |
        """.trimMargin(), outputStream.toString())
    }
}