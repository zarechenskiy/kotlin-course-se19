package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")

    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val expLexer = ExpLexer(CharStreams.fromString("(1 + 2)"))
    println(ExpParser(BufferedTokenStream(expLexer)).eval().value)
}