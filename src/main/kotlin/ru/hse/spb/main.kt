package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser

fun main(args: Array<String>) {
    if (!checkArgs(args)) {
        return
    }

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

private fun checkArgs(args: Array<String>): Boolean {
    if (args.isEmpty()) {
        println("Path to file expected.")
        return false
    }
    if (args.size > 1) {
        println("Unexpected argument.")
        return false
    }
    return true
}