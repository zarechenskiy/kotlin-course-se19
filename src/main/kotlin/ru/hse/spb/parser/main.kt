package ru.hse.spb.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("The only argument should be the path to the program")
    }

    val lexer = FunLangLexer(CharStreams.fromFileName(args[0]))
    val tokens = CommonTokenStream(lexer)
    tokens.fill()
    val parser = FunLangParser(tokens)
    FunInterpreter().visit(parser.file())
}