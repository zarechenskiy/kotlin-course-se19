package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser

object Interpreter {

    private val commentRegex = """//[^\r\n]*""".toRegex()
    private val emptyLine = """^\s*(\r|\n|\r\n|$)""".toRegex(RegexOption.MULTILINE)

    private fun preprocess(program: String) = program.replace(commentRegex, "").replace(emptyLine, "")

    fun run(program: String) {
        val clearedProgram = preprocess(program)
        val lexer = FunCallLexer(CharStreams.fromString(clearedProgram))
        val parser = FunCallParser(CommonTokenStream(lexer))
        parser.file().accept(Visitor())
    }
}

