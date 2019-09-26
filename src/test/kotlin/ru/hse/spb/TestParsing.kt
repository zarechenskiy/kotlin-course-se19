package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert
import org.junit.Test
import ru.hse.spb.parser.MyLangLexer
import ru.hse.spb.parser.MyLangParser

class TestParsing {
    private fun parse(code: String) = MyLangVisitor().apply {
        visitLFile(MyLangParser(BufferedTokenStream(MyLangLexer(CharStreams.fromString(code)))).lFile())
    }

    @Test
    fun `test int variable declaration`() {
        val visitor = parse("""
            var a = 10
            var b = 15
        """.trimIndent())
        Assert.assertEquals(10, visitor.scope.findVariable("a")!!.toInt())
        Assert.assertEquals(15, visitor.scope.findVariable("b")!!.toInt())
        Assert.assertNull(visitor.scope.findVariable("c"))
    }

    @Test
    fun `test bool variable declaration`() {
        val visitor = parse("""
            var a = true
            var b = false
        """.trimIndent())
        Assert.assertEquals(true, visitor.scope.findVariable("a")!!.toBool())
        Assert.assertEquals(false, visitor.scope.findVariable("b")!!.toBool())
        Assert.assertNull(visitor.scope.findVariable("c"))
    }

    @Test
    fun `test variable block`() {
        val visitor = parse("""
            var a = 1
            var b = 2
            {
                var c = 3
            }
        """.trimIndent())
        Assert.assertNotNull(visitor.scope.findVariable("a"))
        Assert.assertNotNull(visitor.scope.findVariable("b"))
        Assert.assertNull(visitor.scope.findVariable("c"))
    }

    @Test
    fun `test fun`() {
        val visitor = parse("""
            fun a() {}
            fun b() {}
        """.trimIndent())
        Assert.assertNotNull(visitor.scope.findFunction("a"))
        Assert.assertNotNull(visitor.scope.findFunction("b"))
        Assert.assertNull(visitor.scope.findFunction("c"))
    }

    @Test
    fun `test fun in block`() {
        val visitor = parse("""
            fun a() {}
            fun b() {}
            {
                fun c() {}
            }
        """.trimIndent())
        Assert.assertNotNull(visitor.scope.findFunction("a"))
        Assert.assertNotNull(visitor.scope.findFunction("b"))
        Assert.assertNull(visitor.scope.findFunction("c"))
    }
}
