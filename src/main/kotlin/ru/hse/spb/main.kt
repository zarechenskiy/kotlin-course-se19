package ru.hse.spb


fun main() {
    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)

        frame("frametitle", "arg1" to "arg2") {
            itemize {
                for (row in 0..1) {
                    item { + "$row text" }
                }
            }

            enumerate {
                for (row in 0..2) {
                    item { + "text" }
                }
            }

            flushleft {
                +"go-go-go!"
            }

            flushright {
                +"go-go-go!"
                +"went..."
            }

            centering {
                +"go-go-go!"
                +"went..."
                +"gone."
            }


            math {
                +"a=b + c"
                +"a=b * c"
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