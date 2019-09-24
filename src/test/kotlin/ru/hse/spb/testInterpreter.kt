package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ParserRuleContext
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser

class TestInterpreter {
    private val ctx = ParserRuleContext()

    private fun intrepretate(program: String, scope: Scope) {
        val expLexer = FunLexer(CharStreams.fromString(program))
        val parser = FunParser(BufferedTokenStream(expLexer))
        val file = parser.file()
        val interpreter = ProgramEvaluationVisitor(scope)
        println()
        interpreter.visit(file)
    }

    @Test
    fun testAssignNumber() {
        val scope = Scope()
        intrepretate(
                "var a = 10",
                scope)
        assertEquals(10, scope.getVariable("a", ctx))
    }

    @Test
    fun testAssignVariable() {
        val scope = Scope()
        intrepretate(
                """
                |var a = 10
                |var b = a
                """.trimMargin(),
                scope
        )
        assertEquals(10, scope.getVariable("b", ctx))
        assertEquals(10, scope.getVariable("a", ctx))
    }

    @Test
    fun testReceiveVariableFromParentScope() {
        val scope = Scope()
        intrepretate(
                """
                |var a = 10
                |var b
                |{
                |    b = a
                |}
                """.trimMargin(),
                scope
        )
        assertEquals(10, scope.getVariable("b", ctx))
        assertEquals(10, scope.getVariable("a", ctx))
    }

    @Test
    fun testVariableHiding() {
        val scope = Scope()
        intrepretate(
                """
                |var a = 10
                |var c
                |{
                |    var a = 20
                |    c = a
                |}
                """.trimMargin(),
                scope
        )
        assertEquals(20, scope.getVariable("c", ctx))
    }

    @Test
    fun testBinaryOperations() {
        val scope = Scope()
        val a = 10
        val b = 20
        scope.addVariable("a", a, ctx)
        scope.addVariable("b", b, ctx)
        scope.addVariable("c", 0, ctx)

        intrepretate(
                "c = a + b",
                scope)
        assertEquals(a + b, scope.getVariable("c", ctx))

        intrepretate(
                "c = a - b",
                scope)
        assertEquals(a - b, scope.getVariable("c", ctx))

        intrepretate(
                "c = a * b",
                scope)
        assertEquals(a * b, scope.getVariable("c", ctx))

        intrepretate(
                "c = a / b",
                scope)
        assertEquals(a / b, scope.getVariable("c", ctx))

        intrepretate(
                "c = a % b",
                scope)
        assertEquals(a % b, scope.getVariable("c", ctx))

        intrepretate(
                "c = a == b",
                scope)
        assertEquals(0, scope.getVariable("c", ctx))

        intrepretate(
                "c = a != b",
                scope)
        assertEquals(1, scope.getVariable("c", ctx))

        intrepretate(
                "c = a < b",
                scope)
        assertEquals(1, scope.getVariable("c", ctx))

        intrepretate(
                "c = a > b",
                scope)
        assertEquals(0, scope.getVariable("c", ctx))

        intrepretate(
                "c = a <= b",
                scope)
        assertEquals(1, scope.getVariable("c", ctx))

        intrepretate(
                "c = a >= b",
                scope)
        assertEquals(0, scope.getVariable("c", ctx))

        intrepretate(
                "c = a && b",
                scope)
        assertEquals(1, scope.getVariable("c", ctx))

        intrepretate(
                "c = a || b",
                scope)
        assertEquals(1, scope.getVariable("c", ctx))
    }

    @Test
    fun testPriorities() {
        val scope = Scope()
        val a = 10
        val b = 20
        scope.addVariable("a", a, ctx)
        scope.addVariable("b", b, ctx)
        scope.addVariable("c", 0, ctx)

        intrepretate(
                "c = a + b * 2",
                scope)
        assertEquals(a + b * 2, scope.getVariable("c", ctx))
    }

    @Test
    fun testConditionalFalse() {
        val scope = Scope()
        val a = 10
        val b = 20
        scope.addVariable("a", a, ctx)
        scope.addVariable("b", b, ctx)
        scope.addVariable("c", 0, ctx)

        intrepretate(
                """if (a > b) {
                    |   c = a
                    |} else {
                    |   c = b
                    |}
                    """.trimMargin(),
                scope)
        assertEquals(b, scope.getVariable("c", ctx))
    }

    @Test
    fun testConditionalTrue() {
        val scope = Scope()
        val a = 20
        val b = 10
        scope.addVariable("a", a, ctx)
        scope.addVariable("b", b, ctx)
        scope.addVariable("c", 0, ctx)

        intrepretate(
                """if (a > b) {
                    |   c = a
                    |} else {
                    |   c = b
                    |}
                    """.trimMargin(),
                scope)
        assertEquals(a, scope.getVariable("c", ctx))
    }

    @Test
    fun testMixedConditional() {
        val scope = Scope()
        scope.addVariable("a", -10, ctx)
        scope.addVariable("b", 5, ctx)
        scope.addVariable("c", 10, ctx)
        scope.addVariable("res", 0, ctx)

        intrepretate(
                """ if ((a != c) && b < c) {
                    |   res = 1
                    |}
                    """.trimMargin(),
                scope)
        assertEquals(1, scope.getVariable("res", ctx))
    }

    @Test
    fun testWhileLoop() {
        val scope = Scope()
        scope.addVariable("i", 0, ctx)

        intrepretate(
                """ while (i < 10) {
                    |   i = i + 1
                    |}
                    """.trimMargin(),
                scope)
        assertEquals(10, scope.getVariable("i", ctx))
    }

    @Test
    fun testFunctionWithNoArguments() {
        val scope = Scope()

        intrepretate(
                """ fun f() {
                    |   return 1
                    |}
                    |
                    |var a = f()
                    """.trimMargin(),
                scope)
        assertEquals(1, scope.getVariable("a", ctx))
    }

    @Test
    fun testFunctionWithoutReturn() {
        val scope = Scope()

        intrepretate(
                """ fun f() { }
                    |var a = f()
                    """.trimMargin(),
                scope)
        assertEquals(0, scope.getVariable("a", ctx))
    }

    @Test
    fun testReturnBeforeStatements() {
        val scope = Scope()

        intrepretate(
                """ var a = 0
                    |
                    |fun f() {
                    |   a = 1
                    |   return 0
                    |   a = 2
                    |}
                    |
                    |f()
                    """.trimMargin(),
                scope)
        assertEquals(1, scope.getVariable("a", ctx))
    }

    @Test
    fun testReturnInsideWhileStatements() {
        val scope = Scope()

        intrepretate(
                """ var a = 0
                    |
                    |fun f() {
                    |   a = 1
                    |   while (a < 10) {
                    |       return 0
                    |       a = 100
                    |   }
                    |   a = 2
                    |}
                    |
                    |f()
                    """.trimMargin(),
                scope)
        assertEquals(1, scope.getVariable("a", ctx))
    }

    @Test
    fun testFunctionWithParameters() {
        val scope = Scope()

        intrepretate(
                """ fun f(a, b, c) {
                    |   return a + b + c
                    |}
                    |
                    |var d = 1
                    |var res = f(1, 2, 10 + 20)
                    """.trimMargin(),
                scope)
        assertEquals(1 + 2 + 10 + 20, scope.getVariable("res", ctx))
    }

    @Test
    fun testCallFunctionFromParentScope() {
        val scope = Scope()
        intrepretate(
                """ fun f() {
                    |   x = 1
                    |}
                    |
                    |var x
                    |{
                    |   f()
                    |}
                """.trimMargin(),
                scope
        )
        assertEquals(1, scope.getVariable("x", ctx))
    }

    @Test
    fun testFunctionHiding() {
        val scope = Scope()
        intrepretate(
                """ fun f() {
                    |   x = 1
                    |}
                    |
                    |var x
                    |{
                    |   fun f() {
                    |       x = 2
                    |   }
                    |   f()
                    |}
                """.trimMargin(),
                scope
        )
        assertEquals(2, scope.getVariable("x", ctx))
    }
}