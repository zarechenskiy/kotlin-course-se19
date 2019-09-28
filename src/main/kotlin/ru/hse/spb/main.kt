package ru.hse.spb


fun main() {
    val file = tex {
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
    }
    file.toOutputStream(System.out)
}