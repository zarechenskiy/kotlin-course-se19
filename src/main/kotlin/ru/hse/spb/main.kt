package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser
import ru.hse.spb.visitors.BlockVisitor

fun main(args: Array<String>) {
    val reader = CharStreams.fromFileName(args[0], Charsets.UTF_8)
    val parser = FunParser(CommonTokenStream(FunLexer(reader)))
    val program = BlockVisitor().visit(parser.file())
    runBlock(program, Scope(null))
}