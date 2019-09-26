package ru.hse.spb

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FPLLexer
import ru.hse.spb.parser.FPLParser

class FplParser {
    companion object {
        fun parse(program: String): FplTree {
            val lexer = FPLLexer(CharStreams.fromString(program))
            val parser = FPLParser(CommonTokenStream(lexer))
            return parser.file().accept(FplParseTreeVisitor()) as FplTree
        }

        fun parse(stream: CharStream): FplTree {
            val lexer = FPLLexer(stream)
            val parser = FPLParser(CommonTokenStream(lexer))
            return parser.file().accept(FplParseTreeVisitor()) as FplTree
        }
    }
}
