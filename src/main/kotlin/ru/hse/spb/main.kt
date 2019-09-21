package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun main(args: Array<String>) {
    args.forEach { file ->
        EvalVisitor().visitFile(ExpParser(BufferedTokenStream(ExpLexer(CharStreams.fromFileName(file)))).file())
    }
}