package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        throw RuntimeException("Need a path to a program!")
    }
    val path = File(args[0]).toPath()
    val stream = CharStreams.fromPath(path)

    println(FplInterpreter().interpret(FplParser.parse(stream)))
}
