package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.util.stream.Stream

internal class TexDslKtTest {

    private lateinit var out: ByteArrayOutputStream

    @BeforeEach
    fun initStream() {
        out = ByteArrayOutputStream()
    }

    @ParameterizedTest
    @MethodSource("testProvider")
    fun emptyDocument(document: TexDocument, expected: String) {

        document.toOutputStream(out)
        assertEquals(expected, out.toString())
    }

    companion object {
        private val rows = listOf("1", "2", "3")
        @JvmStatic
        fun testProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    document { },
                    """
                        \begin{document}
                        \end{document}
                        
                    """.trimIndent()
                ),
                Arguments.of(
                    document {
                        documentClass("article")
                        usepackage("babel")
                    },
                    """
                        \documentclass{article}
                        \usepackage{babel}
                        \begin{document}
                        \end{document}
                        
                    """.trimIndent()
                ),
                Arguments.of(
                    document {
                        documentClass("article")
                        usepackage("babel")

                        itemize {
                            item {
                                math {
                                    //item() // error
                                    +"""\sum"""
                                }
                            }
                            item {}
                        }
                    },
                    """
                        \documentclass{article}
                        \usepackage{babel}
                        \begin{document}
                            \begin{itemize}
                                \item
                                    \begin{math}
                                        \sum
                                    \end{math}

                                \item

                            \end{itemize}
                        \end{document}
                        
                    """.trimIndent()
                ),
                Arguments.of(
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
                                """
                            }
                        }
                    },
                    """
                        \documentclass{beamer}
                        \usepackage{babel, russian}
                        \begin{document}
                            \begin{frame}[arg1=arg2]{frametitle}
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
                        
                    """.trimIndent()
                )
            )
    }
}