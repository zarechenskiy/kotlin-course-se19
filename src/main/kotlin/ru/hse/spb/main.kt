package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.LangLexer
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    require(args.size == 1) { "Only one argument was expected, but ${args.size} found" }
    val lines = File(args[0]).readLines().joinToString(separator = System.lineSeparator())
    val expLexer = LangLexer(CharStreams.fromString(lines))
    while (true) {
        val token = expLexer.nextToken()
        if (token.text != "<EOF>") {
//            println(token.text + " " + token.startIndex + " " + token.stopIndex)
            println(token.text)
        } else {
            break
        }
    }
}
