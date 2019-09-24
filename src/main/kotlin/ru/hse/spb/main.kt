package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser

fun main(args: Array<String>) {
    checkArgs(args)

    val lexer = FunLexer(CharStreams.fromFileName(args[0]))
    val parser = FunParser(CommonTokenStream(lexer))
    val file = parser.file()

    val scope = Scope()
    scope.addFunction("println", { arguments ->
        println(arguments.joinToString(separator = " "))
        0
    }, ParserRuleContext())

    val interpreter = ProgramEvaluationVisitor(scope)
    interpreter.visit(file)
}

private fun checkArgs(args: Array<String>) {
    if (args.isEmpty()) {
        println("Path to file expected.")
    }
    if (args.size > 1) {
        println("Unexpected argument.")
    }
}