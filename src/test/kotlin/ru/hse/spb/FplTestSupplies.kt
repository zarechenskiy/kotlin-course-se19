package ru.hse.spb

import java.util.*

/* Simple `If` test */

val programSimpleIf = """
    var y = 10
    if (y > 20) {
        y = 5
    } else {
        y = 30
    }
    return y
""".trimIndent()

val treeSimpleIf = FplTree(listOf(
        Variable("y", Optional.of(Literal(10))),
        If(Binary(Identifier("y"), Literal(20), BinaryOperation.GT),
                FplTree(listOf(Assign("y", Literal(5)))),
                Optional.of(FplTree(listOf(Assign("y", Literal(30)))))),
        Return(Identifier("y"))
))

// y < 20
val treeSimpleIfOtherCondition = FplTree(listOf(
        Variable("y", Optional.of(Literal(10))),
        If(Binary(Identifier("y"), Literal(20), BinaryOperation.LT),
                FplTree(listOf(Assign("y", Literal(5)))),
                Optional.of(FplTree(listOf(Assign("y", Literal(30)))))),
        Return(Identifier("y"))
))

/* Function test */

val programFunTest = """
            fun foo(a, b) {
                var d = a * b
                return d
            }
            return foo(2, 3)
        """.trimIndent()

val treeFunTest = FplTree(listOf(
        Fun("foo", listOf("a", "b"), FplTree(listOf(
                Variable("d", Optional.of(Binary(Identifier("a"), Identifier("b"),
                        BinaryOperation.MULT))), Return(Identifier("d"))
        ))),
        Return(FunCall("foo", listOf(Literal(2), Literal(3))))
))

/* Inner function definition test */

val programInnerDefTest = """
    fun foo(n) {
        fun bar(m) {
            return n + m
        }
        return bar(1)
    }
    return foo(10)
""".trimIndent()

val treeInnerDefTest = FplTree(listOf(
        Fun("foo", listOf("n"), FplTree(listOf(
                Fun("bar", listOf("m"), FplTree(listOf(
                        Return(Binary(Identifier("n"), Identifier("m"), BinaryOperation.PLUS))
                ))),
                Return(FunCall("bar", listOf(Literal(1))))
        ))),
        Return(FunCall("foo", listOf(Literal(10))))
))

/* Fibonacci test */

val programFibTest = """
            // Comment 1
            fun fib(n) {
                // Comment 2
                if (n <= 1) {
                    return 1
                }
                // Comment 3
                return fib(n - 1) + fib(n - 2)
            } // Comment4
            // Comment 5
            var i = 1 // Comment 4
            while (i <= 5) {
               println(i, fib(i)) // 1 + 2
               i = i + 1
            }
            return fib(i)
        """.trimIndent()


// Не представляю, как это правильно отформатировать.
// Автоформаттер предлагает так.
val treeFibTest = FplTree(listOf(
        Fun("fib", listOf("n"),
                FplTree(listOf(
                        If(Binary(Identifier("n"), Literal(1), BinaryOperation.LE),
                                FplTree(listOf(Return(Literal(1)))), Optional.empty()),
                        Return(Binary(
                                FunCall("fib", listOf(Binary(Identifier("n"), Literal(1), BinaryOperation.MINUS))),
                                FunCall("fib", listOf(Binary(Identifier("n"), Literal(2), BinaryOperation.MINUS))),
                                BinaryOperation.PLUS))))),
        Variable("i", Optional.of(Literal(1))),
        While(Binary(Identifier("i"), Literal(5), BinaryOperation.LE),
                FplTree(listOf(
                        FunCall("println",
                                listOf(Identifier("i"), FunCall("fib", listOf(Identifier("i"))))),
                        Assign("i", Binary(Identifier("i"), Literal(1), BinaryOperation.PLUS))
                ))),
        Return(FunCall("fib", listOf(Identifier("i"))))
))
