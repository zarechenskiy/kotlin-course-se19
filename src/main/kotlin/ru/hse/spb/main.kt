package ru.hse.spb

fun result() = document {
    documentClass("beamer")
    usepackage("babel", "russian" /* varargs */)
    frame(frameTitle="frametitle", param="arg1" to "arg2") {
        itemize {
            for (row in rows) {
                item { + "$row text" }
            }
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
