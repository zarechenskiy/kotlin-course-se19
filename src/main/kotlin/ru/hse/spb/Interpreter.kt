package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser
import java.io.PrintStream

object Interpreter {
    fun interpret(program : String, outputStream: PrintStream = System.out) {
        val lexer = FunLexer(CharStreams.fromString(program))
        val parser = FunParser(CommonTokenStream(lexer))

        parser.file().accept(FunVisitor(outputStream))
    }
}