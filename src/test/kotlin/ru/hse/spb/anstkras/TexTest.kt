package ru.hse.spb.anstkras

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TexTest {
    @Test
    fun testEnumerateAndItemize() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteArrayOutputStream, true, Charsets.UTF_8)
        document {
            documentClass("beamer")
            usePackage("babel", "russian" /* varargs */)
            enumerate {
                item {
                    enumerate {
                        item {
                            +"enumerate"
                        }
                    }
                }
            }

            itemize {
                item {
                    enumerate {
                        item {
                            +"itemize"
                        }
                    }
                }
            }
        }.toOutputStream(printStream)
        val result = String(byteArrayOutputStream.toByteArray(), Charsets.UTF_8)
        val expected = "\\begin{document}\n" +
                "  \\documentclass{beamer}\n" +
                "  \\usepackage{babel}\n" +
                "  \\usepackage{russian}\n" +
                "  \\begin{enumerate}\n" +
                "    \\item\n" +
                "        \\begin{enumerate}\n" +
                "          \\item\n" +
                "              enumerate\n" +
                "        \\end{enumerate}\n" +
                "  \\end{enumerate}\n" +
                "  \\begin{itemize}\n" +
                "    \\item\n" +
                "        \\begin{enumerate}\n" +
                "          \\item\n" +
                "              itemize\n" +
                "        \\end{enumerate}\n" +
                "  \\end{itemize}\n" +
                "\\end{document}\n\n"
        assertEquals(expected, result)
    }

    @Test
    fun testAlign() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteArrayOutputStream, true, Charsets.UTF_8)
        document {
            documentClass("beamer")
            usePackage("babel", "russian" /* varargs */)
            center {
                +"line 1"
                +"line 2"
            }
        }.toOutputStream(printStream)
        val result = String(byteArrayOutputStream.toByteArray(), Charsets.UTF_8)
        val expected = "\\begin{document}\n" +
                "  \\documentclass{beamer}\n" +
                "  \\usepackage{babel}\n" +
                "  \\usepackage{russian}\n" +
                "  \\begin{center}\n" +
                "    line 1\n" +
                "    line 2\n" +
                "  \\end{center}\n" +
                "\\end{document}\n\n"
        assertEquals(expected, result)
    }

    @Test
    fun testCustomTag() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteArrayOutputStream, true, Charsets.UTF_8)
        document {
            customTag("pyglist", "language" to "kotlin") {
                +"""
               |val a = 1
               |
            """.trimMargin()
            }
        }.toOutputStream(printStream)
        val result = String(byteArrayOutputStream.toByteArray(), Charsets.UTF_8)
        val expected = "\\begin{document}\n" +
                "  \\begin{pyglist}[language = kotlin]\n" +
                "    val a = 1\n" +
                "\n" +
                "  \\end{pyglist}\n" +
                "\\end{document}\n\n"
        assertEquals(expected, result)
    }
}
