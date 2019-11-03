package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

fun main(args: Array<String>) {
    val reader = CharStreams.fromFileName(args[0], Charsets.UTF_8)
    val parser = LangParser(CommonTokenStream(LangLexer(reader)))
    evaluate(BlockVisitor().visitFile(parser.file()), State(null))
}