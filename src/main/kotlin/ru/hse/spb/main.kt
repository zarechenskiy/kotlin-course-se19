package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext

import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("First argument should be filename")
    }

    if (args.size > 1) {
        println("too many arguments")
    }

    val lexer = ExpLexer(CharStreams.fromFileName(args[0]))
    val parser = ExpParser(CommonTokenStream(lexer))

    val scopes = Scopes()

    scopes.functionsScope.insert("println", { arguments ->
        println(arguments.joinToString(separator = " "))
        0
    }, ParserRuleContext())

    Visitor(scopes).visit(parser.file())
}