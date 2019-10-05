package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.stream.IntStream.range

class TexDslTest {

    @Test
    fun `smoke test`() {
        assertEquals("""
            \begin{document}
              \documentclass{beamer}
              \usepackage{babel}
              \usepackage{russian}
              \begin{frame}[arg1=arg2]
                \frametitle{frametitle}
                \begin{itemize}
                  \item
                    1 text
                  \item
                    2 text
                  \item
                    3 text
                \end{itemize}
                \begin{pyglist}[language=kotlin]
                  val a = 1
            
                \end{pyglist}
              \end{frame}
            \end{document}
            
        """.trimIndent(), document {
            documentClass("beamer")
            usepackage("babel", "russian")

            frame(frameTitle = "frametitle", attrs = *arrayOf("arg1" to "arg2")) {
                itemize {
                    for (row in range(1, 4)) {
                        item { +"$row text" }
                    }
                }

                customTag(name = "pyglist", attrs = *arrayOf("language" to "kotlin")) {
                    +"""
                        val a = 1
                        
                    """
                }
            }
        }.replace("\r\n", "\n"))
    }

    @Test
    fun `test math`() {
        assertEquals("""
            \begin{document}
              \begin{equation}
                2 * 2 = 4
                \begin{var}
                  x = 5
                \end{var}
              \end{equation}
            \end{document}
            
        """.trimIndent(), document {
            math {
                +"2 * 2 = 4"
                customTag("var") {
                    +"x = 5"
                }
            }
        }.replace("\r\n", "\n"))
    }

    @Test
    fun `test item`() {
        assertEquals("""
            \begin{document}
              \begin{itemize}
                \item
                  \begin{itemize}
                    \item
                      1
                    \item
                      2
                  \end{itemize}
                  \begin{enumerate}
                    \item
                      3
                    \item
                      4
                  \end{enumerate}
                \item
                  5
              \end{itemize}
              \begin{enumerate}
                \item
                  \begin{itemize}
                    \item
                      6
                    \item
                      7 
                  \end{itemize}
                  \begin{enumerate}
                    \item
                      8
                    \item
                      9
                  \end{enumerate}
                \item
                  10
              \end{enumerate}
            \end{document}

        """.trimIndent(), document {
            itemize {
                item {
                    itemize {
                        item { +"1" }
                        item { +"2" }
                    }

                    enumerate {
                        item { +"3" }
                        item { +"4" }
                    }
                }

                item { +"5" }
            }

            enumerate {
                item {
                    itemize {
                        item { +"6" }
                        item { +"7 " }
                    }

                    enumerate {
                        item { +"8" }
                        item { +"9" }
                    }
                }

                item { +"10" }
            }
        }.replace("\r\n", "\n"))
    }

    @Test
    fun `test aligment`() {
        assertEquals("""
            \begin{document}
              \centering
              center
              \raggedright
              left
              \raggedleft
              right
            \end{document}

        """.trimIndent(), document {
            center()
            +"center"
            left()
            +"left"
            right()
            +"right"
        }.replace("\r\n", "\n"))
    }
}