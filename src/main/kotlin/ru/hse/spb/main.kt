package ru.hse.spb

val rows = listOf("First", "Second", "And third")

fun result() = document {
    documentClass("beamer")
    usepackage("babel", "russian" /* varargs */)
    frame(frameTitle="frametitle", param="arg1" to "arg2") {
        itemize {
            for (row in rows) {
                item { + "$row text" }
            }
        }

        align {
            line("1 + 2 ", "= 3")
            line("567 + 1 ", "= 568")
        }

        // begin{pyglist}[language=kotlin]...\end{pyglist}
        customTag(name = "pyglist", param="language" to "kotlin") {
            +"""
               |val a = 1
               |
            """
        }
    }
}.toOutputStream(System.out)


fun main() {
    println(result())
}
