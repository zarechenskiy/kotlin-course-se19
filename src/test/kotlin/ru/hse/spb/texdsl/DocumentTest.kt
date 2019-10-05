package ru.hse.spb.texdsl

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DocumentTest {
    private fun compareResult(expected: String, test: Document) {
        assertEquals(expected + "\n", test.toString())
    }

    @Test
    fun testDocumentClassWithNoArgs() {
        val test =
            document {
                documentClass("article")
            }

        val expected = """
            \documentclass{article}
            \begin{document}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testDocumentClassWithArgs() {
        val test =
            document {
                documentClass("article", "russian")
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testSeveralDocumentClassesThrowsException() {
        assertThrows(TexException::class.java) {
            document {
                documentClass("article", "russian")
                documentClass("article", "russian")
            }
        }
    }

    @Test
    fun testUsePackage() {
            val test =
                document {
                    documentClass("article", "russian")
                    usepackage("noArgumentPackage")
                    usepackage("additionalArgumentPackage", "language" to "russian", "indent" to "noindent")
                }

            val expected = """
            \documentclass{article}[russian]
            \usepackage{noArgumentPackage}
            \usepackage[language=russian,indent=noindent]{additionalArgumentPackage}
            \begin{document}
            \end{document}
        """.trimIndent()

            compareResult(expected, test)
    }

    @Test
    fun testFrame() {
        val test =
            document {
                documentClass("article", "russian")

                frame {
                    +"555"
                }

                frame("title1") {
                    + "555"
                }

                frame("title2", "arg1" to "val1", "arg2" to "val2") {
                    + "555"
                }

                frame("title3", "val1", "val2") {
                    + "555"
                }

                frame("title4") {
                    addOptionalParameters("arg1" to "val1", "arg2" to "val2")
                    + "555"
                }

                frame("title5") {
                    addOptionalParameters("val1", "val2")
                    + "555"
                }

                frame("frametitle" to "title", "something" to "anything") {
                    addOptionalParameters("val1", "val2")
                    + "555"
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
                \begin{frame}
                    555
                \end{frame}
                \begin{frame}{title1}
                    555
                \end{frame}
                \begin{frame}{title2}[arg1=val1,arg2=val2]
                    555
                \end{frame}
                \begin{frame}{title3}[val1,val2]
                    555
                \end{frame}
                \begin{frame}{title4}[arg1=val1,arg2=val2]
                    555
                \end{frame}
                \begin{frame}{title5}[val1,val2]
                    555
                \end{frame}
                \begin{frame}{frametitle=title,something=anything}[val1,val2]
                    555
                \end{frame}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testEnumerate() {
        val test =
            document {
                documentClass("article", "russian")
                usepackage("enumitem")

                enumerate {
                    item{ +"item1" }
                    item{ +"item2" }
                }

                enumerate {
                    addOptionalParameters("label" to "1")
                    item{+"item1"}
                }

                enumerate("someparameter" to "somevalue") {
                    addOptionalParameters("label" to "1")
                    item{+"item1"}
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \usepackage{enumitem}
            \begin{document}
                \begin{enumerate}
                    \item
                        item1
                    \item
                        item2
                \end{enumerate}
                \begin{enumerate}[label=1]
                    \item
                        item1
                \end{enumerate}
                \begin{enumerate}{someparameter=somevalue}[label=1]
                    \item
                        item1
                \end{enumerate}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testItemize() {
        val test =
            document {
                documentClass("article", "russian")
                usepackage("enumitem")

                itemize {
                    item{ +"item1" }
                    item{ +"item2" }
                }

                itemize {
                    addOptionalParameters("label" to "1")
                    item{+"item1"}
                }

                itemize("someparameter" to "somevalue") {
                    addOptionalParameters("label" to "1")
                    item{+"item1"}
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \usepackage{enumitem}
            \begin{document}
                \begin{itemize}
                    \item
                        item1
                    \item
                        item2
                \end{itemize}
                \begin{itemize}[label=1]
                    \item
                        item1
                \end{itemize}
                \begin{itemize}{someparameter=somevalue}[label=1]
                    \item
                        item1
                \end{itemize}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testMath() {
        val test =
            document {
                documentClass("article", "russian")
                math {
                    + "\\sqrt[2]{2}"
                }
                math {
                    addOptionalParameters("align" to "right")
                    + "\\sqrt[2]{2}"
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
                \begin{displaymath}
                    \sqrt[2]{2}
                \end{displaymath}
                \begin{displaymath}[align=right]
                    \sqrt[2]{2}
                \end{displaymath}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testFlushes() {
        val test =
            document {
                documentClass("article", "russian")
                flushleft {
                }
                flushright {
                }
                center {
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
                \begin{flushleft}
                \end{flushleft}
                \begin{flushright}
                \end{flushright}
                \begin{center}
                \end{center}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testCustomTag() {
        val test =
            document {
                documentClass("article", "russian")

                customTag("myTag1") {
                    +"555"
                }

                customTag("myTag2") {
                    + "555"
                }

                customTag("myTag3", "arg1" to "val1", "arg2" to "val2") {
                    + "555"
                }

                customTag("myTag4", "val1", "val2") {
                    + "555"
                }

                customTag("myTag5") {
                    addOptionalParameters("arg1" to "val1", "arg2" to "val2")
                    + "555"
                }

                customTag("myTag6") {
                    addOptionalParameters("val1", "val2")
                    + "555"
                }

                customTag("myTag7", "arg1" to "val1", "arg2" to "val2") {
                    addOptionalParameters("val1", "val2")
                    + "555"
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
                \begin{myTag1}
                    555
                \end{myTag1}
                \begin{myTag2}
                    555
                \end{myTag2}
                \begin{myTag3}{arg1=val1,arg2=val2}
                    555
                \end{myTag3}
                \begin{myTag4}{val1,val2}
                    555
                \end{myTag4}
                \begin{myTag5}[arg1=val1,arg2=val2]
                    555
                \end{myTag5}
                \begin{myTag6}[val1,val2]
                    555
                \end{myTag6}
                \begin{myTag7}{arg1=val1,arg2=val2}[val1,val2]
                    555
                \end{myTag7}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }

    @Test
    fun testEnumerateInsideItem() {
        val test =
            document {
                documentClass("article", "russian")

                enumerate {
                    item { enumerate {item {+"item2"}} }
                }
            }

        val expected = """
            \documentclass{article}[russian]
            \begin{document}
                \begin{enumerate}
                    \item
                        \begin{enumerate}
                            \item
                                item2
                        \end{enumerate}
                \end{enumerate}
            \end{document}
        """.trimIndent()

        compareResult(expected, test)
    }
}