package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser
import ru.hse.spb.visitors.BlockVisitor
import java.io.FileInputStream

class ParserTests {

    private val testDirectory = "src/test/data"

    private fun doParserTest(testName: String) {
        val reader = CharStreams.fromFileName("$testDirectory/$testName.fun", Charsets.UTF_8)
        val parser = FunParser(CommonTokenStream(FunLexer(reader)))
        val program = BlockVisitor().visit(parser.file())
        assertEquals(
            String(FileInputStream("$testDirectory/$testName.parse").readBytes()),
            program.toString()
        )
    }

    @Test
    fun testExpressions() {
        doParserTest("expressions")
    }

    @Test
    fun testIf() {
        doParserTest("if")
    }

    @Test
    fun testNestedFunctions() {
        doParserTest("nestedFunctions")
    }

    @Test
    fun testRecursion() {
        doParserTest("recursion")
    }
}