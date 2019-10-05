package ru.hse.spb


fun main() {
    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
//        frame(frameTitle="frametitle") {
//            itemize {
//                for (row in rows) {
//                    item { + "$row text" }
//                }
//            }
//
//            // begin{pyglist}[language=kotlin]...\end{pyglist}
//            customTag(name = "pyglist") {
//                +"""
//                    |val a = 1
//                    |
//                """
//            }
//        }
    }.toOutputStream(System.out)
}