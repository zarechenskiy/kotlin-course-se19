package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser

private val program = """
    |println(1)
""".trimMargin()

fun main() {
    val lexer = FunCallLexer(CharStreams.fromString(program))
    val parser = FunCallParser(CommonTokenStream(lexer))

    parser.file().accept(Visitor())
}