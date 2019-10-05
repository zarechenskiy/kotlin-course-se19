package tanvd.itmo.kotlin.tex

import junit.framework.Assert.assertEquals
import org.junit.Test
import tanvd.itmo.kotlin.tex.schema.document

class TestTex {

    @Test
    fun `frames test`() {
        assertEquals(
                """
                |\begin{document}
                |  \begin{frame}[arg1 = value1]
                |    \frametitle{my_frame}
                |    Here is frame 1
                |  \end{frame}
                |  \begin{frame}
                |    \frametitle{other_frame}
                |    \begin{enumerate}
                |      \item
                |        4
                |        5
                |    \end{enumerate}
                |  \end{frame}
                |\end{document}
                |""".trimMargin(),
                document {
                    frame("my_frame", "arg1" to "value1") {
                        +"Here is frame 1"
                    }
                    frame("other_frame") {
                        enumerate {
                            item {
                                +"4"
                                +"5"
                            }
                        }
                    }
                }.render()
        )
    }

    @Test
    fun `enumeration test`() {
        assertEquals(
                """
                |\begin{document}
                |  \begin{itemize}
                |    \item
                |      1
                |      2
                |    \item
                |      3
                |  \end{itemize}
                |  \begin{enumerate}
                |    \item
                |      4
                |      5
                |  \end{enumerate}
                |\end{document}
                |""".trimMargin(),
                document {
                    itemize {
                        item {
                            +"1"
                            +"2"
                        }
                        item {
                            +"3"
                        }
                    }
                    enumerate {
                        item {
                            +"4"
                            +"5"
                        }
                    }
                }.render()
        )
    }

    @Test
    fun `prepared example`() {
        val rows = arrayOf("one", "bam", "lom")
        assertEquals(
                """ 
                |\documentclass{beamer}
                |\usepackage{babel, russian}
                |\begin{document}
                |  \begin{frame}[arg1 = arg2]
                |    \frametitle{frametitle}
                |    \begin{itemize}
                |      \item
                |        one text
                |      \item
                |        bam text
                |      \item
                |        lom text
                |    \end{itemize}
                |    \begin{pyglist}[language = kotlin]
                |      val a = 1
                |    \end{pyglist}
                |  \end{frame}
                |\end{document}
                |""".trimMargin(),
                document {
                    documentClass("beamer")
                    usepackage("babel", "russian" /* varargs */)
                    frame("frametitle", "arg1" to "arg2") {
                        itemize {
                            for (row in rows) {
                                item { +"$row text" }
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
                }.render()
        )
    }
}
