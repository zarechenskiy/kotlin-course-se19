package ru.hse.spb

import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        error("Wrong number of arguments.")
    }

    interpret(File(args[0]).readText())
}