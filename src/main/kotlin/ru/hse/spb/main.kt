package ru.hse.spb

fun main() {
    val rows = listOf("12", "34")
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
    }.toOutputStream(System.out)
}