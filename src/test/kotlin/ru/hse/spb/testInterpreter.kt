package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileWriter
import java.io.PrintStream
import java.lang.Exception
import java.util.*

internal fun runTestOnFile(content: String, expected: String?, testParser: Boolean = false) {
    val byteStream = ByteArrayOutputStream()
    val output = PrintStream(byteStream)

    if (!testParser) {
        System.setOut(output)
    } else {
        System.setErr(output)
    }

    val file = createTempFile()

    val writer = FileWriter(file)
    writer.write(content)

    writer.flush()

    if (!testParser) {
        main(arrayOf(file.absolutePath))
    } else {
        runParserOnFile(file)
    }

    if (expected != null) {
        output.flush()
        byteStream.flush()

        val scanner = Scanner(ByteArrayInputStream(byteStream.toByteArray()))
        val result = StringBuilder()

        if (scanner.hasNextLine()) {
            while (true) {
                result.append(scanner.nextLine())

                if (scanner.hasNextLine()) {
                    result.append('\n')
                } else {
                    break
                }
            }
        }

        assertEquals(expected, result.toString())
    }
}

internal fun testThrowingException(content: String, expected: String) {
    testThrowingException(content, expected, CompileException::class.java)
}

internal fun testThrowingException(content: String, expected: String, expressionClass: Class<out Exception>) {
    val exception = assertThrows(expressionClass) {
        runTestOnFile(content, null)
    }

    assertEquals(expected, exception.message)
}

class MainKtTest {
    @Test
    fun testCommentSupport() {
        val content = """
            var a = 20 //comment
            var b = 30 //comment var c = 40
            //comment on the empty string
            println(a)
            println(b)//end of line comment
         """.trimIndent()

        val expected = "20\n30"

        runTestOnFile(content, expected)
    }

    @Test
    fun testFunctionScopesInsideBlockAndOnlyInsideBlock() {
        val content = """
            if (1) {
                fun f(n) {
                    return n
                }
                
                println(f(1))
            }
            
            println(f(2))
         """.trimIndent()

        val expected = "Line 9: unknown function f"

        testThrowingException(content, expected)
    }

    @Test
    fun testVariableAndFunctionReDefinition() {
        val content = """
            fun f() {
                return 1
            }
            
            var t = 2
            
            fun bar() {
                var t = 3
                
                fun t() {
                    return 4
                }
                
                println(t)
                println(t())
            }
            
            bar()
         """.trimIndent()

        val expected = "3\n4"

        runTestOnFile(content, expected)
    }

    @Test
    fun testUndefinedVariable() {
        val content = """
            fun f() {
                a = 5
            }
            
            println(a)
         """.trimIndent()

        val expected = "Line 5: unknown variable a"

        testThrowingException(content, expected)
    }

    @Test
    fun testUndefinedFunction() {
        val content = """
            fun f() {
                fun g() {
                }
                
                a = 5
            }
            
            println(g())
         """.trimIndent()

        val expected = "Line 8: unknown function g"

        testThrowingException(content, expected)
    }

    @Test
    fun testFunctionWithoutReturn() {
        val content = """
            fun f() {
            }
            
            println(f())
         """.trimIndent()

        val expected = "0"

        runTestOnFile(content, expected)
    }

    @Test
    fun testFunctionSemantic() {
        val content = """
            fun f(a, b) {
                fun g(a) {
                    return a + 1
                }
                
                println(a, b, g(a), g(b))
            }
            
            f(1, 2)
         """.trimIndent()

        val expected = "1 2 2 3"

        runTestOnFile(content, expected)
    }

    @Test
    fun testVariableWithoutAssigment() {
        val content = """
            var a
            println(a)
         """.trimIndent()

        val expected = "null"

        runTestOnFile(content, expected)
    }

    @Test
    fun testVariableWithAssigment() {
        val content = """
            var a
            a = 5
            println(a)
         """.trimIndent()

        val expected = "5"

        runTestOnFile(content, expected)
    }

    @Test
    fun testFunctionWithDuplicatedNames() {
        val content = """
            fun foo(a, a) {
            }
            
            foo(1, 1)
         """.trimIndent()

        val expected = "Line 1: duplicate variable a definition"

        testThrowingException(content, expected)
    }

    @Test
    fun testWhileSemantic() {
        val content = """
            var i = 1
            while (i <= 3) {
                println(i)
                i = i + 1
            }
         """.trimIndent()

        val expected = "1\n2\n3"

        runTestOnFile(content, expected)
    }

    @Test
    fun testIfWithoutElse() {
        val content = """
            if (0) {
                println(1)
            }
            println(2)
         """.trimIndent()

        val expected = "2"

        runTestOnFile(content, expected)
    }

    @Test
    fun testIfWithElse() {
        val content = """
            if (0) {
                println(1)
            } else {
                println(3)
            }
            println(2)
         """.trimIndent()

        val expected = "3\n2"

        runTestOnFile(content, expected)
    }

    @Test
    fun testReAssigment() {
        val content = """
            var a = 1
            a = 2
            println(a)
         """.trimIndent()

        val expected = "2"

        runTestOnFile(content, expected)
    }

    @Test
    fun testReturnInTheMiddle() {
        val content = """
            fun foo(n) {
                return 0
                
                println(5)
            }
            
            foo(1)
         """.trimIndent()

        val expected = ""

        runTestOnFile(content, expected)
    }

    @Test
    fun testBinaryExpressionOrder() {
        val content = """
            println(2 * 2 + 2 <= 2 * (2 + 2))
         """.trimIndent()

        val expected = "1"

        runTestOnFile(content, expected)
    }

    @Test
    fun testMixedBinaryExpression() {
        val content = """
            fun foo(n) {
                return n + 1
            }
            
            var a = 1
            var b = 2
            
            println(((foo(foo(a) + foo(2))) + 3))
         """.trimIndent()

        val expected = "9"

        runTestOnFile(content, expected)
    }

    @Test
    fun testAllBinaryOperations() {
        val content = """
            var a = 1
            var b = 2
            
            println(a + b)
            println(a * b)
            println(a / b)
            println(a % b)
            println(a > b)
            println(a < b)
            println(a >= b)
            println(a <= b)
            println(a == b)
            println(a != b)
            println(a || b)
            println(a && b)
         """.trimIndent()

        val expected = "3\n" +
                "2\n" +
                "0\n" +
                "1\n" +
                "0\n" +
                "1\n" +
                "0\n" +
                "1\n" +
                "0\n" +
                "1\n" +
                "1\n" +
                "1"

        runTestOnFile(content, expected)
    }

    @Test
    fun testDivisionByZero() {
        val content = """
            println(1 / 0)
         """.trimIndent()

        assertThrows(java.lang.ArithmeticException::class.java) {runTestOnFile(content, null)}
    }

    @Test
    fun testInput1() {
        val content = "var a = 10\n" +
                "var b = 20\n" +
                "if (a > b) {\n" +
                "    println(1)\n" +
                "} else {\n" +
                "    println(0)\n" +
                "}"

        val expected = "0"

        runTestOnFile(content, expected)
    }

    @Test
    fun testInput2() {
        val content = "fun fib(n) {\n" +
                "    if (n <= 1) {\n" +
                "        return 1\n" +
                "    }\n" +
                "    return fib(n - 1) + fib(n - 2)\n" +
                "}\n" +
                "\n" +
                "var i = 1\n" +
                "while (i <= 5) {\n" +
                "    println(i, fib(i))\n" +
                "    i = i + 1\n" +
                "}"

        val expected = "1 1\n" +
                "2 2\n" +
                "3 3\n" +
                "4 5\n" +
                "5 8"

        runTestOnFile(content, expected)
    }

    @Test
    fun testInput3() {
        val content = "fun foo(n) {\n" +
                "    fun bar(m) {\n" +
                "        return m + n\n" +
                "    }\n" +
                "\n" +
                "    return bar(1)\n" +
                "}\n" +
                "\n" +
                "println(foo(41)) // prints 42"

        val expected = "42"

        runTestOnFile(content, expected)
    }
}