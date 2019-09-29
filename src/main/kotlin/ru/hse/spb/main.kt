package ru.hse.spb

fun main() {
    document {
        documentClass("beamer")
        usepackage("babel", "russian")
        frame("first slide", "arg1" to "arg2", "arg3" to "arg4", "arg5" to "arg6") {

            customTag("pyglist", "language" to "kotlin") {
                +"""
               |val a = 1
               |
            """
            }

            itemize {
                item {
                    +"This is my first point."
                    customTag("pyglist", "language" to "kotlin") {
                        +"""
                       |val a = 42
                       |
                    """
                    }
                }
                item {
                    +"This is my second point."
                }
            }
        }
        frame("second slide") {
            enumerate {
                item {
                    +"This is my first point."
                }
                item {
                    +"This is my second point."
                }
            }
        }
    }.toOutputStream(System.out)
}