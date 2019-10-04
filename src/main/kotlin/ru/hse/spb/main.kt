package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunPLLexer
import ru.hse.spb.parser.FunPLParser


var oldProgram = "fun a(b) {if (a > 0) {a(0)}}"

fun main(parameters : Array<String>) {
    val lexer = FunPLLexer(CharStreams.fromFileName(parameters[0]))
    val parser = FunPLParser(CommonTokenStream(lexer))
    parser.file().accept(StatementsEvaluationVisitor())

}


