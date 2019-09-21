package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.ToylangLexer
import ru.hse.spb.parser.ToylangParser


fun main(args: Array<String>) {
    if (args.isEmpty())
        error("Specify input file")

    val lexer = ToylangLexer(CharStreams.fromFileName(args[0]))
    val parser = ToylangParser(CommonTokenStream(lexer))
    val interpreter = ToylangInterpreter(
        listOf(Pair("println", { list -> println(list.joinToString { it.toString() }).let { 0 } }))
    )
    parser.file().accept(interpreter)
}
