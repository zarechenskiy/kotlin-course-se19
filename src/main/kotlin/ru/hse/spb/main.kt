package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser
import java.io.File

fun removeComments(code: List<String>): List<String> {
    return code.map { line -> line.substring(0, ("$line//").indexOf("//")) }
}

private fun getTypes(code: String): List<Int> {
    val lexer = LangLexer(CharStreams.fromString(code))

    val tokens: MutableList<Token> = ArrayList()

    while (!lexer._hitEOF) {
        val token = lexer.nextToken()
        if (token.type == LangParser.WS) {
            continue
        }
        tokens.add(token)
    }

    for (t in tokens) {
        println("LangParser.${LangParser.VOCABULARY.getSymbolicName(t.type)},")
    }

    return tokens.map { it.type }
}

fun main(args: Array<String>) {
//    require(args.size == 1) { "Only one argument was expected, but ${args.size} found" }
//    val code = removeComments(File(args[0]).readLines()).joinToString(separator = System.lineSeparator())
//    val lexer = LangLexer(CharStreams.fromString(code))
//    val parser = LangParser(CommonTokenStream(lexer))
//    parser.file().block().accept(BlockVisitor())
    val code =
        "var a = 10\n" +
                "println((a + 1) / (a - 1)) * 101 % 3)\n"

    getTypes(code)
}
