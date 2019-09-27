package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.*
import java.util.*

internal fun runParserOnFile(file: File) {
    val lexer = ExpLexer(CharStreams.fromFileName(file.absolutePath))
    val parser = ExpParser(CommonTokenStream(lexer))

    val scopes = Scopes()

    scopes.functionsScope.insert("println", { arguments ->
        println(arguments.joinToString(separator = " "))
        0
    }, ParserRuleContext())

    parser.file()
}

class ParserTest {
    @Test
    fun testCorrectIdentifierNames() {
        val content = """
            var a
            var abcd000
            var a_5
            var variable
            var whileWhile
         """.trimIndent()

        runTestOnFile(content, "")
    }

    @Test
    fun testInCorrectIdentifierNames() {
        val content = """
            var a
            var var
         """.trimIndent()

        val expected = """
            line 2:4 missing IDENTIFIER at 'var'
            line 2:7 missing IDENTIFIER at '<EOF>'
        """.trimIndent()

        runTestOnFile(content, expected, true)
    }

    @Test
    fun testCorrectLiteral() {
        val content = """
            println(1)
            println(0)
            println(-5)
            println(-0)
            print(1234567890)
         """.trimIndent()

        val expected = """
        """.trimIndent()

        runTestOnFile(content, expected, true)
    }

    @Test
    fun testInCorrectLiteral() {
        val content = """
            println(05)
            println(--0)
         """.trimIndent()

        val expected = """
            line 1:9 extraneous input '5' expecting ')'
            line 2:8 extraneous input '-' expecting {'(', IDENTIFIER, LITERAL}
        """.trimIndent()

        runTestOnFile(content, expected, true)
    }

    //То, что всё остальное парсится как нужно, я считаю, достаточно проверено в тестах интерпретатора.
}