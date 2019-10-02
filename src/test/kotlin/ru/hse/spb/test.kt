package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.*

class TestSource {
    fun Document.assertEquals(text: String) {
        val elements = this.toTex().split(Regex("\\s")).filter { it.isNotBlank() && it.isNotEmpty() }
        val expectedElements = text.split(Regex("\\s")).filter { it.isNotBlank() && it.isNotEmpty() }
        assertEquals(expectedElements, elements)
    }

    @Test
    fun emptyDocumentTest() {
        document {
        }.assertEquals("""
            \begin{document}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun documentWithTextTest() {
        document {
            +"A"
            +"B"
        }.assertEquals("""
            \begin{document}
            A
            B
            \end{document}
        """.trimIndent())
    }

    @Test
    fun usePackageTest() {
        document {
            usepackage("amsmath")
        }.assertEquals("""
            \usepackage{amsmath}
            \begin{document}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun usePackageAdditionalArgsTest() {
        document {
            usepackage("babel", "russian")
        }.assertEquals("""
            \usepackage[russian]{babel}
            \begin{document}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun documentClassTest() {
        document {
            documentClass("article")
        }.assertEquals("""
            \documentclass{article}
            \begin{document}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun documentClassSeveralArgsTest() {
        document {
            documentClass("article", "12pt", "a5paper")
        }.assertEquals("""
            \documentclass[12pt, a5paper]{article}
            \begin{document}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun emptyCustomTagTest() {
        document {
            customTag("tag") {}
        }.assertEquals("""
            \begin{document}
                \begin{tag}
                \end{tag}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun flushRightTest() {
        document {
            flushRight {
                +"Kek"
            }
        }.assertEquals("""
            \begin{document}
            \begin{flushright}
            Kek
            \end{flushright}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun flushLeftTest() {
        document {
            flushLeft {
                +"Kek"
            }
        }.assertEquals("""
            \begin{document}
            \begin{flushleft}
            Kek
            \end{flushleft}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun centerTest() {
        document {
            center {
                +"Kek"
            }
        }.assertEquals("""
            \begin{document}
            \begin{center}
            Kek
            \end{center}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun mathModeTest() {
        document {
            math {
                +"(1 + 2 + 3) * 4"
            }
        }.assertEquals("""
            \begin{document}
            \begin{math}
            (1 + 2 + 3) * 4
            \end{math}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun itemizeTest() {
        document {
            itemize {
                for (i in (1..3)) {
                    item { +i.toString() }
                }
            }
        }.assertEquals("""
            \begin{document}
            \begin{itemize}
                \item 1
                \item 2
                \item 3
            \end{itemize}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun enumerateTest() {
        document {
            enumerate {
                for (i in listOf("a", "b")) {
                    item { +i }
                }
            }
        }.assertEquals("""
            \begin{document}
            \begin{enumerate}
                \item a
                \item b
            \end{enumerate}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun innerItemizeTest() {
        document {
            enumerate {
                item {
                    itemize {
                        item {
                            +"aga"
                        }
                    }
                }
            }
        }.assertEquals("""
            \begin{document}
            \begin{enumerate}
                \item 
                \begin{itemize}
                    \item aga
                \end{itemize}
            \end{enumerate}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun frameTest() {
        document {
            frame("frame") {
                +"Text"
            }
        }.assertEquals("""
            \begin{document}
            \begin{frame}{frame}
                Text
            \end{frame}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun frameParametersTest() {
        document {
            frame("frame", "kek" to "lel") {
                +"Text"
            }
        }.assertEquals("""
            \begin{document}
            \begin{frame}[kek=lel]{frame}
                Text
            \end{frame}
            \end{document}
        """.trimIndent())
    }

    @Test
    fun outputToStreamTest() {
        val stream = ByteArrayOutputStream()
        document {
        }.toOutputStream(PrintStream(stream))
        assertEquals("""
            
            \begin{document}
            
            \end{document}
        
        """.trimIndent(),
                stream.toString())
    }
}