package ru.hse.spb

import ru.hse.spb.texdsl.document
import java.io.OutputStreamWriter

fun main() {
    document {
        documentClass("beamer")
        usePackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1" to "arg2") {
            itemize {
                for (row in 1..5) {
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
    }.render(OutputStreamWriter(System.out))
}