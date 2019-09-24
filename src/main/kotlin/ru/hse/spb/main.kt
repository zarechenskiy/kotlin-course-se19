package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.hse.spb.funterpreter.EvaluationVisitor
import ru.hse.spb.funterpreter.runProgram
import ru.hse.spb.parser.FunGrammarLexer
import ru.hse.spb.parser.FunGrammarParser

private val testProgram = """
    var x = 0 
    fun f(xx) {
        println(x)
        x = 2
        return x + 1
    }
    println(f(x))
    return x
""".trimIndent()

// TODO line comments, tests, main, docs

fun main(args: Array<String>) {
    try {
        val result = runProgram(testProgram)
        println("Returned: ${result.returned}")
        println("Value: ${result.value}")
        println("Exception: ${result.exception}")
    } catch (e: ParseCancellationException) {
        println(e.message)
        println("Execution finished due to parse error")
        return
    }
}
