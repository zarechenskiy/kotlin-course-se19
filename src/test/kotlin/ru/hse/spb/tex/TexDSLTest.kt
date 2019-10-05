package ru.hse.spb.tex

import org.junit.Assert.assertEquals
import org.junit.Test

class TexDSLTest {
    @Test
    fun dumbTest() {
        val expTex = """
            \begin{document}
             sample_text
            \end{document}

        """.trimIndent()

        val resTex = document {
            +"sample_text"
        }.toString()

        assertEquals(expTex, resTex)
    }

    @Test
    fun argsTest() {
        val expTex = """
            \begin{document}
             \begin{frame}[a=b, c=d]
             \end{frame}
            \end{document}

        """.trimIndent()

        val resTex = document {
            frame(null,"a" to "b", "c" to "d") {}
        }.toString()

        assertEquals(expTex, resTex)
    }

    @Test
    fun complexTest() {
        val expTex = """
            \documentclass{best_class}
            \usepackage{the_most_beautiful_package, inglesh}
            \begin{document}
             \begin{frame}[a=b]
              \frametitle{first}
              \begin{itemize}
               \item
                1
               \item
                2
              \end{itemize}
             \end{frame}
             \begin{enumerate}
              \item
               item_in_enumerate
             \end{enumerate}
             \begin{flushleft}
              alignment_text
             \end{flushleft}
            \end{document}

        """.trimIndent()

        val resTex = document {
            usepackage("the_most_beautiful_package", "inglesh")
            documentClass("best_class")
            frame("first", "a" to "b") {
                itemize {
                    item {
                        +"1"
                    }
                    item {
                        +"2"
                    }
                }
            }
            enumerate {
                item {
                    +"item_in_enumerate"
                }
            }
            flushleft {
                +"alignment_text"
            }
        }.toString();

        assertEquals(expTex, resTex)
    }


}