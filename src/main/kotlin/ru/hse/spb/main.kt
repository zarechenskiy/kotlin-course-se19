package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import java.io.File
import java.rmi.UnexpectedException


fun main(args: Array<String>) {
    val fileName = args[0]
    if (File(fileName).exists()) {
        val lexer = FunCallLexer(CharStreams.fromFileName(args[0]))
        val parser = FunCallParser(BufferedTokenStream(lexer))
        parser.file().accept(StatementsEvaluationVisitor())
    } else {
        throw UnexpectedException("file $fileName is not exists")
    }
}
