package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import java.io.File

fun main(args: Array<String>) {
    val filename = args[0]
    val lexer = FunCallLexer(CharStreams.fromFileName(filename))
    val parser = FunCallParser(CommonTokenStream(lexer))
    parser.file().accept(Visitor())
}

