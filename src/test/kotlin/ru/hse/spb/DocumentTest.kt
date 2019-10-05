package ru.hse.spb

import org.junit.jupiter.api.Test

class DSLTeXTest {

    @Test
    fun exampleTest() {
        val rows = listOf("qwe", "rty", "uio")

        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("arg1" to "arg2", frameTitle = "frameTitle") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("language" to "kotlin", name = "pyglist") {
                    +"""
               |val a = 1
               |
            """.trimMargin()
                }
            }
        }.toOutputStream(System.out)
    }
}