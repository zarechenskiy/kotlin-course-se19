package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.Token
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class LexerTest {

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

        return tokens.map { it.type }
    }

    @Test
    fun ifScenario() {
        val code = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        val expectedTypes: List<Int> = listOf(
            LangParser.VARIABLE,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.LITERAL,
            LangParser.VARIABLE,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.LITERAL,
            LangParser.IF,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.GT,
            LangParser.IDENTIFIER,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RBRACE,
            LangParser.ELSE,
            LangParser.LBRACE,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RBRACE
        )

        assertArrayEquals(expectedTypes.toIntArray(), getTypes(code).toIntArray())
    }

    @Test
    fun fibScenario() {
        val code = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            
            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """.trimIndent()

        val expectedTypes: List<Int> = listOf(
            LangParser.FUNCTION,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.IF,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.LE,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.RET,
            LangParser.LITERAL,
            LangParser.RBRACE,
            LangParser.RET,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.MINUS,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.PLUS,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.MINUS,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RBRACE,
            LangParser.VARIABLE,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.LITERAL,
            LangParser.WHILE,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.LE,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.SEPARATOR,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.RPAREN,
            LangParser.RPAREN,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.IDENTIFIER,
            LangParser.PLUS,
            LangParser.LITERAL,
            LangParser.RBRACE
        )

        assertArrayEquals(expectedTypes.toIntArray(), getTypes(code).toIntArray())
    }

    @Test
    fun nestedFunScenario() {
        val code = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
            
                return bar(1)
            }
            
            println(foo(41))
        """.trimIndent()

        val expectedTypes: List<Int> = listOf(
            LangParser.FUNCTION,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.FUNCTION,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.RET,
            LangParser.IDENTIFIER,
            LangParser.PLUS,
            LangParser.IDENTIFIER,
            LangParser.RBRACE,
            LangParser.RET,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RBRACE,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RPAREN
        )

        assertArrayEquals(expectedTypes.toIntArray(), getTypes(code).toIntArray())
    }

    @Test
    fun complexIfScenario() {
        val code = """
            fun f() {
                var a = 10
                if ((50 < a && a < 100) || (a % 10 == 0)) {
                    return 1
                }
            }
            
            println(f())
        """.trimIndent()

        val expectedTypes: List<Int> = listOf(
            LangParser.FUNCTION,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.VARIABLE,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.LITERAL,
            LangParser.IF,
            LangParser.LPAREN,
            LangParser.LPAREN,
            LangParser.LITERAL,
            LangParser.LT,
            LangParser.IDENTIFIER,
            LangParser.AND,
            LangParser.IDENTIFIER,
            LangParser.LT,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.OR,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.REMINDER,
            LangParser.LITERAL,
            LangParser.EQ,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RPAREN,
            LangParser.LBRACE,
            LangParser.RET,
            LangParser.LITERAL,
            LangParser.RBRACE,
            LangParser.RBRACE,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.RPAREN,
            LangParser.RPAREN
        )

        assertArrayEquals(expectedTypes.toIntArray(), getTypes(code).toIntArray())
    }

    @Test
    fun complexMathExpressionScenario() {
        val code = """
            var a = 10
            println((a + 1) / (a - 1)) * 101 % 3)
        """.trimIndent()

        val expectedTypes: List<Int> = listOf(
            LangParser.VARIABLE,
            LangParser.IDENTIFIER,
            LangParser.ASSIGNMENT,
            LangParser.LITERAL,
            LangParser.IDENTIFIER,
            LangParser.LPAREN,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.PLUS,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.DIVISION,
            LangParser.LPAREN,
            LangParser.IDENTIFIER,
            LangParser.MINUS,
            LangParser.LITERAL,
            LangParser.RPAREN,
            LangParser.RPAREN,
            LangParser.ASTERISK,
            LangParser.LITERAL,
            LangParser.REMINDER,
            LangParser.LITERAL,
            LangParser.RPAREN
        )

        assertArrayEquals(expectedTypes.toIntArray(), getTypes(code).toIntArray())
    }
}
