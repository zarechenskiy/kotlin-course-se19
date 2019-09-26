package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunInterpreterLexer
import ru.hse.spb.parser.FunInterpreterParser

val program = """
""".trimMargin()

 fun main(args: Array<String>) {
    val lexer = FunInterpreterLexer(CharStreams.fromString(program))
    val parser = FunInterpreterParser(CommonTokenStream(lexer))
    parser.file().accept(StatementsEvaluatorVisitor(mapOf()))
}