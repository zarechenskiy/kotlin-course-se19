package ru.hse.spb

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TexBuilderTests {

    fun Document.string(): String {
        val os = ByteArrayOutputStream()
        val ps = PrintStream(os)
        this.toOutputStream(ps)
        return os.toString()
    }

    @Test
    fun testFrame() {
        assertEquals(
                """
                |\begin{document}
                |\begin{frame}[arg1=arg2]
                |\frametitle{frame1}
                |FirstFrame
                |\end{frame}
                |\begin{frame}
                |\frametitle{other_frame}
                |\begin{enumerate}
                |\item
                |first
                |second
                |\end{enumerate}
                |\end{frame}
                |\end{document}
                |""".trimMargin(),
                document {
                    frame("frame1", "arg1" to "arg2") {
                        +"FirstFrame"
                    }
                    frame("other_frame") {
                        enumerate {
                            item {
                                +"first"
                                +"second"
                            }
                        }
                    }
                }.string()
        )
    }

    @Test
    fun itemizeTest() {
        assertEquals(
                """
                |\begin{document}
                |\begin{itemize}
                |\item
                |1
                |\item
                |2
                |3
                |\end{itemize}
                |\end{document}
                |""".trimMargin(),
                document {
                    itemize {
                        item {
                            +"1"
                        }
                        item {
                            +"2"
                            +"3"
                        }
                    }
                }.string()
        )
    }

    @Test
    fun enumerateTest() {
        assertEquals(
                """
                    |\begin{document}
                    |\begin{enumerate}
                    |\item
                    |1
                    |\item
                    |2
                    |3
                    |\end{enumerate}
                    |\end{document}
                    |""".trimMargin(),
                document {
                    enumerate {
                        item {
                            +"1"
                        }
                        item {
                            +"2"
                            +"3"
                        }
                    }
                }.string()
        )
    }

    @Test
    fun testExample() {
        val rows = arrayOf("one", "bam", "lom")
        assertEquals(
                """ 
                |\begin{document}
                |\documentclass{beamer}
                |\usepackage{babel,russian}
                |\begin{frame}[arg1=arg2]
                |\frametitle{frametitle}
                |\begin{itemize}
                |\item
                |one text
                |\item
                |bam text
                |\item
                |lom text
                |\end{itemize}
                |\begin{pyglist}[language=kotlin]
                |val a = 1
                |\end{pyglist}
                |\end{frame}
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
                }.string()
        )
    }
}