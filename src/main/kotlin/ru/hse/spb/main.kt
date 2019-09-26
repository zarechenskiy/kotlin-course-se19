package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.GrammarLexer
import ru.hse.spb.parser.GrammarParser
import java.lang.IllegalStateException

/** Takes one argument -- file path. Specified file will be interpreted. */
fun main(args: Array<String>) {
    if (args.size != 1) {
        error("One argument expected: file path.")
    }

    val lexer = GrammarLexer(CharStreams.fromFileName(args[0]))
    val parser = GrammarParser(CommonTokenStream(lexer))
    try {
        parser.file().accept(EvaluationVisitor())
    } catch (e: IllegalStateException) {
        println(e.message)
    }
}
