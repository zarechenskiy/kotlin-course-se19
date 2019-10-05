import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.hse.spb.document
import java.io.ByteArrayOutputStream

class DSLTest {

    private val stream = ByteArrayOutputStream()

    @Before
    fun init() {
        stream.flush()
    }

    @Test
    fun simpleTags() {
        document {
            documentClass("doc class")
            usepackage("pac1", "pac2", "pac3")
            frame("frame title", "x" to "y") {

            }
        }.toOutputStream(stream)
        val expected = """
            \begin{document}
                \documentclass{doc class}
                \usepackage{pac1}
                \usepackage{pac2}
                \usepackage{pac3}
                \begin{frame}[x=y]
                    \frametitle{frame title}
                \end{frame}
            \end{document}
            
        """.trimIndent()
        assertEquals(expected, stream.toString())
    }

    @Test
    fun tagsWithBlock() {
        val rows = arrayListOf("row1", "row2", "...", "row100!!!!")
        document {
            documentClass("doc class")
            usepackage()
            flushleft {
                frame("title", "a" to "b", "c" to "d", "e" to "f") {
                    enumerate {
                        item {
                            math("F_n = F_{n-1} + F_{n-2}")
                        }
                    }
                    flushright {
                        itemize {
                            center {
                                customTag("my_awesome tag", "c" to "d", "x" to "y", "time" to "10am") {

                                }
                            }
                        }
                    }
                }
            }
            itemize {
                for (row in rows) {
                    item {
                        +row
                    }
                }
            }
        }.toOutputStream(stream)
        val expected = """
            \begin{document}
                \documentclass{doc class}
                \begin{flushleft}
                    \begin{frame}[a=b,c=d,e=f]
                        \frametitle{title}
                        \begin{enumerate}
                            \item
                                ${'$'}F_n = F_{n-1} + F_{n-2}${'$'}
                        \end{enumerate}
                        \begin{flushright}
                            \begin{itemize}
                                \begin{center}
                                    \begin{my_awesome tag}[c=d,x=y,time=10am]
                                    \end{my_awesome tag}
                                \end{center}
                            \end{itemize}
                        \end{flushright}
                    \end{frame}
                \end{flushleft}
                \begin{itemize}
                    \item
                        row1
                    \item
                        row2
                    \item
                        ...
                    \item
                        row100!!!!
                \end{itemize}
            \end{document}

        """.trimIndent()
        assertEquals(expected, stream.toString())
    }

    @Test
    fun sample() {
        val rows = arrayListOf("formula", "message", "theorem")
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
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
        }.toOutputStream(stream)
        val expected = """
            \begin{document}
                \documentclass{beamer}
                \usepackage{babel}
                \usepackage{russian}
                \begin{frame}[arg1=arg2]
                    \frametitle{frametitle}
                    \begin{itemize}
                        \item
                            formula text
                        \item
                            message text
                        \item
                            theorem text
                    \end{itemize}
                    \begin{pyglist}[language=kotlin]
                        
                           |val a = 1
                           |
                        
                    \end{pyglist}
                \end{frame}
            \end{document}
            
        """.trimIndent()
        assertEquals(expected, stream.toString())
    }
}
