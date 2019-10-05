package ru.hse.spb.tex

import java.io.PrintWriter

fun main(args: Array<String>) {
    document {
        usepackage("the_most_beautiful_package", "inglesh")
        documentClass("best_class")
        frame("first", "a" to "b") {
            itemize {
                item {
                    +"1"
                }
                item {
                    +"2"
                }
            }
        }
        enumerate {
            item {
                +"item_in_enumerate"
            }
        }
        flushleft {
            +"alignment_text"
        }
    }.toOutputStream(PrintWriter(System.out))
}