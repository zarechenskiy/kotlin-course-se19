package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.MyLangLexer
import ru.hse.spb.parser.MyLangParser

fun main(args: Array<String>) {
    for (path in args) {
        MyLangVisitor().visitLFile(MyLangParser(BufferedTokenStream(MyLangLexer(CharStreams.fromFileName(path)))).lFile())
    }
}
