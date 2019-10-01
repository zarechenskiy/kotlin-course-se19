package ru.hse.spb

import ru.hse.spb.tex.document

fun main() {
    val doc = document {
        documentClass("beamer")

        usepackage("cmap")
        usepackage("fontenc", "T2A")
        usepackage("babel", "russian")
        usepackage("inputenc", "utf8")
        usepackage("amsmath, amssymb")
        usepackage("enumerate")

        customTag("frame") {
            +"123123"
        }
        
        customTag("frame") {
            enumerate {
                item("33") {
                    itemize {
                        item { +"1" }
                        item { +"2" }
                        item("ad") { +"12" }
                        item("kek3") { +"kek" }
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