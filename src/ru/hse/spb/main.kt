package ru.hse.spb

import ru.hse.spb.tex.document

fun main() {
    val doc = document {
        documentClass("beamer")

        usepackage("cmap")
        usepackage["T2A"]("fontenc")
        usepackage["russian"]("babel")
        usepackage["utf8"]("inputenc")
        usepackage("amsmat", "amssymb")
        usepackage("enumerate")

        customTag("frame") {
            +"123123"
            command("color")("blue")
        }
        
        customTag("frame") {
            enumerate {
                item["33"] {
                    itemize {
                        item { +"1" }
                        item { +"2" }
                        item["ad"] { +"12" }
                        item["kek3"] {
                            +"kek"
                            +"kek"
                        }
                        item { +"dva" }
                    }
                }
                item { +"2" }
                item { +"3" }
            }
        }
    }
    print(doc)
}