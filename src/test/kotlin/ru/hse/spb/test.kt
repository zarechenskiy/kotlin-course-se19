package ru.hse.spb

import junit.framework.Assert.assertEquals
import org.junit.Test

class TexDSLTest {
    @Test
    fun testDocFromExample() {
        val tex = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2", "arg3" to "arg4") {

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("pyglist", "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """
                }
            }
        }.toString()

        assertEquals("""
            \documentclass{beamer}
            \usepackage{babel, russian}
            \begin{document}
              \begin{frame}[arg1 = arg2, arg3 = arg4]
                \frametitle{frametitle}
                \begin{pyglist}[language = kotlin]
                  val a = 1
            
                \end{pyglist}
              \end{frame}
            \end{document}
            
        """.trimIndent(), tex)
    }

    @Test
    fun testDocWithItemizeAndEnumerate() {
        val tex = document {
            frame("first slide", "arg1" to "arg2", "arg3" to "arg4", "arg5" to "arg6") {
                itemize {
                    item {
                        +"This is my first point."
                        customTag("pyglist", "language" to "kotlin") {
                            +"""
                       |val a = 42
                       |
                    """
                        }
                    }
                    item {
                        +"This is my second point."
                    }
                }
            }
            frame("second slide") {
                enumerate {
                    item {
                        +"This is my first point."
                    }
                    item {
                        +"This is my second point."

                        math {
                            +"2^2 - 8 + \\sum_{i = 1}^n i = x"
                        }
                    }
                }
            }
        }.toString()

        assertEquals("""
            \begin{document}
              \begin{frame}[arg1 = arg2, arg3 = arg4, arg5 = arg6]
                \frametitle{first slide}
                \begin{itemize}
                  \item
                    This is my first point.
                    \begin{pyglist}[language = kotlin]
                      val a = 42
            
                    \end{pyglist}
                  \item
                    This is my second point.
                \end{itemize}
              \end{frame}
              \begin{frame}
                \frametitle{second slide}
                \begin{enumerate}
                  \item
                    This is my first point.
                  \item
                    This is my second point.
                    \begin{math}
                      2^2 - 8 + \sum_{i = 1}^n i = x
                    \end{math}
                \end{enumerate}
              \end{frame}
            \end{document}

        """.trimIndent(), tex)
    }

    @Test
    fun testDocWithAlignment() {
        val tex = document {
            frame("first slide") {
                flushleft {
                    +"This is left point."
                }

                flushright {
                    +"This is right point."
                }

                center {
                    +"This is center point."
                }
            }
        }.toString()

        assertEquals("""
            \begin{document}
              \begin{frame}
                \frametitle{first slide}
                \begin{flushleft}
                  This is left point.
                \end{flushleft}
                \begin{flushright}
                  This is right point.
                \end{flushright}
                \begin{center}
                  This is center point.
                \end{center}
              \end{frame}
            \end{document}

        """.trimIndent(), tex)
    }
}