package ru.hse.spb

import java.util.*
import java.util.function.BinaryOperator

data class FplTree(val statements: List<Statement>)

sealed class Statement

/**
 * Assign a new value to existing variable.
 * If variable does not exist throw an exception?
 */
data class Assign(val identifier: String,
                  val expr: Expr) : Statement()

/**
 * If statement. Probably with Else branch
 */
data class If(val condition: Expr,
              val thenClause: FplTree,
              val elseClause: Optional<FplTree>) : Statement()

/**
 * While loop.
 * Contains a condition and a code block.
 */
data class While(val condition: Expr, val body: FplTree) : Statement()

/**
 * Returns an expression.
 */
data class Return(val expr: Expr) : Statement()

/**
 *
 * Variable definition. The only place where we add a
 * variable into out environment. Probably with assignment.
 */
data class Variable(val identifier: String, val value: Optional<Expr>) : Statement()

/**
 * Function Definition
 */
data class Fun(val identifier: String, val parameters: List<String>, val body: FplTree) : Statement()


/**
 * Expression class!
 */
sealed class Expr : Statement()

data class Binary(val left: Expr, val right: Expr, val op: BinaryOperation) : Expr()
data class FunCall(val identifier: String, val arguments: List<Expr>) : Expr()
data class Literal(val literal: Int) : Expr()
data class Identifier(val identifier: String) : Expr()

enum class BinaryOperation : BinaryOperator<Int> {
    // Arithmetic operations
    MULT {
        override fun apply(a: Int, b: Int): Int = a * b
    },
    DIV {
        override fun apply(a: Int, b: Int): Int = a / b
    },
    PLUS {
        override fun apply(a: Int, b: Int): Int = a + b
    },
    MINUS {
        override fun apply(a: Int, b: Int): Int = a - b
    },
    // Logic operations
    CONJ {
        override fun apply(a: Int, b: Int): Int = (a.toBoolean() || b.toBoolean()).toInt()
    },
    DISJ {
        override fun apply(a: Int, b: Int): Int = (a.toBoolean() && b.toBoolean()).toInt()
    },
    // Compare
    EQ {
        override fun apply(a: Int, b: Int): Int = (a == b).toInt()
    },
    NEQ {
        override fun apply(a: Int, b: Int): Int = (a != b).toInt()
    },
    GT {
        override fun apply(a: Int, b: Int): Int = (a > b).toInt()
    },
    GE {
        override fun apply(a: Int, b: Int): Int = (a >= b).toInt()
    },
    LT {
        override fun apply(a: Int, b: Int): Int = (a < b).toInt()
    },
    LE {
        override fun apply(a: Int, b: Int): Int = (a <= b).toInt()
    };

    companion object {
        fun operatorToName(op: String): BinaryOperation = when (op) {
            "*" -> MULT
            "/" -> DIV
            "+" -> PLUS
            "-" -> MINUS
            "==" -> EQ
            "!=" -> NEQ
            "<=" -> LE
            "<" -> LT
            ">=" -> GE
            ">" -> GT
            "||" -> DISJ
            "&&" -> CONJ
            else -> throw RuntimeException("No such an operator: $op")
        }
    }

}

