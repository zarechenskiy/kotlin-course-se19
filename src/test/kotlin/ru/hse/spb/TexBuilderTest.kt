package ru.hse.spb

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class TexBuilderTest {

    fun TeX.string(): String {
        val os = ByteArrayOutputStream()
        val ps = PrintStream(os)
        this.toOutputStream(ps)
        return os.toString()
    }

    @Test
    fun testEmptyFile() {
        Assert.assertEquals("", tex {}.string())
    }

    @Test
    fun testPackage() {
        Assert.assertEquals(
            """
                \usepackage{foo,bar}
                
            """.trimIndent(),
            tex {
                usepackage("foo", "bar")
            }.string()
        )
    }

    @Test
    fun testDocumentClass() {
        Assert.assertEquals(
            """
                \documentclass{class}
                
            """.trimIndent(),
            tex {
                documentClass("class")
            }.string()
        )
    }

    @Test
    fun testDocument() {
        Assert.assertEquals(
            """
                \begin{document}
                \end{document}
                
            """.trimIndent(),
            tex {
                document {

                }
            }.string()
        )
    }

    @Test
    fun testFrame() {
        Assert.assertEquals(
            """
                \begin{frame}[foo=bar]
                \frametitle{test}
                \end{frame}
                
            """.trimIndent(),
            tex {
                frame("test", "foo" to "bar") {

                }
            }.string()
        )
    }

    @Test
    fun testMath() {
        Assert.assertEquals(
            """
                \begin{document}
                \begin{math}
                a=b + c
                a=b * c
                \end{math}
                \end{document}
                
            """.trimIndent(),
            tex {
                document {
                    math {
                        +"a=b + c"
                        +"a=b * c"
                    }
                }
            }.string()
        )
    }

    @Test
    fun testEnumerateAndItemize() {
        Assert.assertEquals(
            """
                \begin{document}
                \begin{enumerate}
                \item
                1
                \item
                \begin{itemize}
                \item
                2.1
                \item
                2.2
                \end{itemize}
                \end{enumerate}
                \end{document}
                
            """.trimIndent(),
            tex {
                document {
                    enumerate {
                        item {
                            +"1"
                        }
                        item {
                            itemize {
                                item {
                                    +"2.1"
                                }
                                item {
                                    +"2.2"
                                }
                            }
                        }
                    }
                }
            }.string()
        )
    }

    @Test
    fun testAll() {
        Assert.assertEquals(
            """
                \documentclass{beamer}
                \usepackage{babel,russian}
                \begin{frame}[arg1=arg2]
                \frametitle{test}
                \begin{itemize}
                \item
                0 text
                \item
                1 text
                \end{itemize}
                \begin{pyglist}[language=kotlin]
                |val a = 1
                |
                \end{pyglist}
                \end{frame}

            """.trimIndent(),
            tex {
                documentClass("beamer")
                usepackage("babel", "russian")
                frame("test", "arg1" to "arg2") {
                    itemize {
                        for (row in 0..1) {
                            item { +"$row text" }
                        }
                    }

                    // begin{pyglist}[language=kotlin]...\end{pyglist}
                    customTag("pyglist", "language" to "kotlin") {
                        +"""
                           |val a = 1
                           |
                        """.trimIndent()
                    }
                }
            }.string()
        )
    }
}