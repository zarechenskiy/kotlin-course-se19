package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.interpreter.ProgrammeInterpreter
import ru.hse.spb.parser.ProgrammeLexer
import ru.hse.spb.parser.ProgrammeParser

private val program = """
    |var a = 2
    |
    |fun foo(b, c) {
    |   if (2 < 3) {
    |       return b + c
    |   }
    |   return 3
    |}
    |
    |a = foo(3, 2)
""".trimMargin()

fun main(args: Array<String>) {
    val lexer = ProgrammeLexer(CharStreams.fromString(program))
    val parser = ProgrammeParser(CommonTokenStream(lexer))
    val interpretator = ProgrammeInterpreter(parser)
    interpretator.run()
    print(interpretator.programmeVisitor.scope.getVariable("a"))
}