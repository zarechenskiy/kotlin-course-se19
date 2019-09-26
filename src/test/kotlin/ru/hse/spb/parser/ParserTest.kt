package ru.hse.spb.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Test

class ParserTest {
    fun parserTree(program: String): String {
        val lexer = FunLangLexer(CharStreams.fromString(program))
        val tokens = CommonTokenStream(lexer)
        tokens.fill()
        val parser = FunLangParser(tokens)
        return parser.file().toStringTree(parser)
    }

    @Test
    fun testFunc() {
        val program = """
            fun f(n) {
                if (n) {
                    return n
                }
            }
        """.trimIndent()

        val expectedTree = """
            (file
            (block
            (statement
            (function fun f (
            (parameterNames n) )
            (blockWithBraces {
            (block
            (statement
            (ifSt if (
            (expr n) )
            (blockWithBraces {
            (block
            (statement
            (returnSt return (expr n))))
            }))))
            })))))
        """.trimIndent().replace('\n', ' ')

        assertEquals(expectedTree, parserTree(program))
    }

    @Test
    fun testVarAndIf() {
        val program = """
            var a
                if (a) {

                }
                a = 1
        """.trimIndent()

        val expectedTree = """
            (file
            (block
            (statement
            (variable var a))
            (statement
            (ifSt if (
            (expr a) )
            (blockWithBraces {
            block
            })))
            (statement
            (assign a =
            (expr 1)))))
        """.trimIndent().replace('\n', ' ')

        assertEquals(expectedTree, parserTree(program))
    }
}