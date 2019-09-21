package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert
import org.junit.Test
import ru.hse.spb.parser.ToylangLexer
import ru.hse.spb.parser.ToylangParser

class ToylangInterpreterTest {

    private fun execute(code: String, block: (ToylangInterpreter) -> Unit = { _ -> Unit }): String {
        val lexer = ToylangLexer(CharStreams.fromString(code))
        val parser = ToylangParser(CommonTokenStream(lexer))

        val out = mutableListOf<String>()

        val interpreter = ToylangInterpreter(
            listOf(Pair("println",
                { list -> list.joinToString { it.toString() }.let { out.add(it); 0 } }
            ))
        )

        parser.file().accept(interpreter)
        block(interpreter)
        return out.joinToString(separator = "\n")
    }

    @Test
    fun testIfBranch() {
        Assert.assertEquals(
            "0", execute(
                """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()
            ) {
                Assert.assertEquals(it.lookupVar("a"), 10)
                Assert.assertEquals(it.lookupVar("b"), 20)
            }
        )
    }

    @Test
    fun testFib() {
        Assert.assertEquals(
            """
                1, 1
                2, 2
                3, 3
                4, 5
                5, 8
            """.trimIndent(), execute(
                """
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
            ) {
                Assert.assertEquals(it.lookupVar("i"), 6)
                Assert.assertNotNull(it.lookupFun("fib"))
            }
        )
    }

    @Test
    fun testCapturing() {
        Assert.assertEquals(
            """
                42
            """.trimIndent(), execute(
                """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
            
                return bar(1)
            }
        
            println(foo(41)) // prints 42
        """.trimIndent()
            ) {
                Assert.assertNotNull(it.lookupFun("foo"))
            }
        )
    }

    @Test
    fun testRedeclarationInInnerScope() {
        execute(
            """
            var a = 1
            var b
            var c
            
            fun foo() {
                var a = 2
                fun bar() {
                    var a = 3
                    b = a
                }
                c = a
                bar()
            }

            foo()
        """.trimIndent()
        ) {
            Assert.assertEquals(3, it.lookupVar("b"))
            Assert.assertEquals(2, it.lookupVar("c"))
            Assert.assertEquals(1, it.lookupVar("a"))
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testRedeclaration() {
        execute(
            """
            var a = 1
            var a = 2
        """.trimIndent()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun testUndeclared() {
        execute(
            """
            a = 1
        """.trimIndent()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun testUndeclaredFun() {
        execute(
            """
            foo()
        """.trimIndent()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun testRedeclaredFun() {
        execute(
            """
            fun foo() {}
            
            fun bar() {
                fun foo() {}
            }
            
            bar()
        """.trimIndent()
        )
    }
}