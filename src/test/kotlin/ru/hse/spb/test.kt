package ru.hse.spb


import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

class TestSource {
    private fun toTestString(doc: Document): String {
        val baos = ByteArrayOutputStream()
        val outStream = PrintStream(baos, true, "UTF-8")
        doc.toOutputStream(outStream)
        return String(baos.toByteArray(), StandardCharsets.UTF_8)
    }

    @Test
    fun general() {
        Assert.assertEquals(
            """
            \begin{document}
                \documentClass{beamer}
                \usepackage{babel, russian}
                \begin{frame}[arg1=arg2]
                    \frametitle{frametitle}
                    \begin{itemize}
                        \item
                        0 text
                        \item
                        1 text
                    \end{itemize}
                    \begin{enumerate}
                        \item
                        0. text
                        \item
                        1. text
                        \item
                        2. text
                    \end{enumerate}
                    \begin{flushleft}
                        go-go-go!
                    \end{flushleft}
                    \begin{flushright}
                        go-go-go!
                        went...
                    \end{flushright}
                    \begin{centring}
                        go-go-go!
                        went...
                        gone.
                    \end{centring}
                    \begin{math}
                        a=b + c
                        a=b * c
                    \end{math}
                    \begin{pyglist}[language=kotlin]
                        
                                       |val a = 1
                                       |
                                        
                    \end{pyglist}
                \end{frame}
            \end{document}

            """.trimIndent(),
            toTestString(
                document {
                    documentClass("beamer")
                    usepackage("babel", "russian" /* varargs */)

                    frame("frametitle", "arg1" to "arg2") {
                        itemize {
                            for (row in 0..1) {
                                item { +"$row text" }
                            }
                        }
                        enumerate {
                            for (row in 0..2) {
                                item { +"text" }
                            }
                        }
                        flushleft {
                            +"go-go-go!"
                        }
                        flushright {
                            +"go-go-go!"
                            +"went..."
                        }
                        centering {
                            +"go-go-go!"
                            +"went..."
                            +"gone."
                        }
                        math {
                            +"a=b + c"
                            +"a=b * c"
                        }
                        // begin{pyglist}[language=kotlin]...\end{pyglist}
                        customTag("pyglist", "language" to "kotlin") {
                            +"""
                           |val a = 1
                           |
                            """
                        }
                    }
                }
            )
        )
    }

    @Test
    fun customTag() {
        Assert.assertEquals(
            """
            \begin{document}
                \documentClass{apple}
                \usepackage{babel}
                \begin{frame}[a=b]
                    \frametitle{THEFRAME}
                    \begin{flushleft}
                        do something
                        do another
                    \end{flushleft}
                    \begin{STRANGE}[I=apple]
                        eat strange apples
                    \end{STRANGE}
                \end{frame}
            \end{document}
            
            """.trimIndent(),
            toTestString(
                document {
                    documentClass("apple")
                    usepackage("babel" /* varargs */)
                    frame("THEFRAME", "a" to "b") {
                        flushleft {
                            +"do something"
                            +"do another"
                        }
                        customTag("STRANGE", "I" to "apple") {
                            +"eat strange apples"
                        }
                    }
                }
            )
        )
    }

    @Test
    fun SameNested() {
        Assert.assertEquals(
            """
            \begin{document}
            \end{document}
            
            """.trimIndent(),
            toTestString(
                document {
                    document {
                        usepackage("package")
                    }
                }
            )
        )
    }

    @Test
    fun optsPlusItemizeAndEnumerate() {
        Assert.assertEquals(
            """
            \begin{document}
                \documentClass{beamer}
                \usepackage{babel, russian, russian}
                \begin{frame}[arg1=arg2, arg3=arg4]
                    \frametitle{firstFrame}
                    \begin{enumerate}
                        \item
                        0. text
                        \item
                        1. text
                        \item
                        2. text
                    \end{enumerate}
                    \begin{itemize}
                        \item
                        0 text
                        \item
                        1 text
                    \end{itemize}
                    \begin{enumerate}
                        \item
                        0. text
                        \item
                        1. text
                        \item
                        2. text
                    \end{enumerate}
                \end{frame}
                \begin{frame}
                    \frametitle{secondFrame}
                    \begin{itemize}
                        \item
                        text
                        \item
                        text
                        \item
                        text
                    \end{itemize}
                    \begin{enumerate}
                        \item
                        0. 0 text
                        \item
                        1. 1 text
                    \end{enumerate}
                \end{frame}
            \end{document}

            """.trimIndent(),
            toTestString(
                document {
                    documentClass("beamer")
                    usepackage("babel", "russian", "russian" /* varargs */)

                    frame("firstFrame", "arg1" to "arg2", "arg3" to "arg4") {
                        enumerate {
                            for (row in 0..2) {
                                item { +"text" }
                            }
                        }
                        itemize {
                            for (row in 0..1) {
                                item { +"$row text" }
                            }
                        }
                        enumerate {
                            for (row in 0..2) {
                                item { +"text" }
                            }
                        }
                    }

                    frame("secondFrame") {
                        itemize {
                            for (row in 0..2) {
                                item { +"text" }
                            }
                        }
                        enumerate {
                            for (row in 0..1) {
                                item { +"$row text" }
                            }
                        }
                    }
                }
            )
        )
    }
}