package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunPLLexer
import ru.hse.spb.parser.FunPLParser

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val lexer = FunPLLexer(CharStreams.fromString("fun a(b) {if (a > 0) {a(0)}}"))
    FunPLParser(BufferedTokenStream(lexer)).file()

    println(CommonTokenStream(lexer).tokens)
    println(getGreeting())
}