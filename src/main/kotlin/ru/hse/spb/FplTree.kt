package ru.hse.spb

import java.util.*

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

enum class BinaryOperation(private val operation: (Int, Int) -> Int) : (Int, Int) -> Int {
    // Arithmetic operations
    MULT({ a, b -> a * b }),
    DIV({ a, b -> a / b }),
    PLUS({ a, b -> a + b }),
    MINUS({ a, b -> a - b }),
    // Logic operations
    CONJ({ a, b -> (a.toBoolean() || b.toBoolean()).toInt() }),
    DISJ({ a, b -> (a.toBoolean() && b.toBoolean()).toInt() }),
    // Compare
    EQ({ a, b -> (a == b).toInt() }),
    NEQ({ a, b -> (a != b).toInt() }),
    GT({ a, b -> (a > b).toInt() }),
    GE({ a, b -> (a >= b).toInt() }),
    LT({ a, b -> (a < b).toInt() }),
    LE({ a, b -> (a <= b).toInt() });

    override fun invoke(a: Int, b: Int): Int = operation(a, b)

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
