package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser
import java.io.File

fun removeComments(code: List<String>): List<String> {
    return code.map { line -> line.substring(0, ("$line//").indexOf("//")) }
}

fun main(args: Array<String>) {
    require(args.size == 1) { "Only one argument was expected, but ${args.size} found" }
    val code = removeComments(File(args[0]).readLines()).joinToString(separator = System.lineSeparator())
    val expLexer = LangLexer(CharStreams.fromString(code))
    val expParser = LangParser(CommonTokenStream(expLexer))
//    expParser.file().accept(CustomVisitor<Void>())
    println(expParser.file().accept(BinaryExpressionVisitor()))
//    for (i in 0 until 13) {
//        println(LangParser.VOCABULARY.getSymbolicName(expLexer.nextToken().type))
//    }
}
