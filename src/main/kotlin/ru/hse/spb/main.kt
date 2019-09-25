package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.hse.spb.funterpreter.runProgram
import java.io.FileInputStream

fun main(args: Array<String>) {
    val inputStream = when (args.size) {
        0 -> System.`in`.also { System.err.println("No file provided, interpreting standard input") }
        1    -> FileInputStream(args[0])
        else -> FileInputStream(args[0]).also { System.err.println("Additional arguments ignored") }
    }

    val charsStream = CharStreams.fromStream(inputStream)

    try {
        val result = runProgram(charsStream)
        System.err.println("Execution finished")
        println("Returned: ${result.returned}")
        println("Value: ${result.value}")
        println("Exception: ${result.exception}")
    } catch (e: ParseCancellationException) {
        println(e.message)
        System.err.println("Execution finished due to parse error")
        return
    }
}