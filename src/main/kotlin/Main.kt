package ru.hse.spb


fun main() {
    val rows = arrayListOf("a", "b", "c")
    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1" to "val1", "arg2" to "val2") {
            itemize {
                for (row in rows) {
                    item { + "$row text" }
                }
            }

            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language" to "kotlin", "fontsize" to "11px", "align" to "auto") {
                +"""
                |val a = 1
                |
                """
            }

            flushleft {
                math("a + b")

                center {
                    itemize {
                        item {
                            math("a + b = c")
                        }
                    }
                }
            }

            flushright {
                flushleft {
                    flushright {
                        itemize {
                            item {
                                customTag("tag", "x" to "y") {

                                }
                            }
                        }
                    }
                }
            }
        }
    }.toOutputStream(System.out)
}
