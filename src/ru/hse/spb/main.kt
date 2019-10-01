package ru.hse.spb

import ru.hse.spb.tex.document


fun main() {
    val doc = document {
        documentClass("beamer")

        usepackage("cmap")
        usepackage["T2A"]("fontenc")
        usepackage["russian"]("babel")
        usepackage["utf8"]("inputenc")
        initCommand("textwidth=160mm")
        def("INF")("\\infty")
        usepackage("amsmat", "amssymb")
        usepackage("enumerate")

        customTag("frame") {
            +"123123"
            command("color")("blue")
        }
        
        customTag("frame") {
            itemTag("enumerate") {
                item["33"] {
                    itemize {
                        item { +"1" }
                        item { +"2" }
                        item["ad"] { +"12" }
                        item["kek3"] {
                            +"kek"
                            +"kek"
                        }
                        item
                        item { +"dva" }
                    }
                }
                item { +"2" }
                item { +"3" }
            }
        }


        customTag("frame") {
            customManualNewlineTag("tabular")("|c | c | c | c |") {
                command("hline")
                +"\\texttt { eps / param } & 1             & 2       & 0.5         "
                command("hline")
                +"0.1                  & 10            & 4       & 100         "
                command("hline")
                +"0.001                & 1000          & 32      & 1000000     "
                command("hline")
                +"1e-05                & 100000        & 317     & 10000000000 "
                command("hline")
                +"1e-07                & 10000000      & 3163    & $10^{ 14 }$   "
                command("hline")
                +"1e-12                & 1000000000000 & 1000000 & $10^{ 24 }$   "
                command("hline")
            }
        }
    }
    print(doc.stringRepresentation)
}