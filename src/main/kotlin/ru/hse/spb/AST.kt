package ru.hse.spb

/* block: (statement)* */
class Block(val body: List<Statement>) {
    override fun equals(other: Any?): Boolean {
        return if (other is Block) {
            body == other.body
        } else {
            false
        }
    }
}

/* statement: function | variable | expression | while | if | assignment | return */
sealed class Statement

data class Function(val identifier: String, val parameters: List<String>, val body: Block) : Statement()

data class Variable(val identifier: String, val value: Expression?) : Statement()

data class While(val condition: Expression, val body: Block) : Statement()

data class If(val condition: Expression, val thenBody: Block, val elseBody: Block) : Statement()

data class Assignment(val identifier: String, val expression: Expression) : Statement()

data class Return(val expression: Expression) : Statement()

/* expression: functionCall | binaryExpression | IDENTIFIER | LITERAL | '(' expression ')' */
sealed class Expression : Statement()

data class FunctionCall(val identifier: String, var parameters: List<Expression>) : Expression()

data class BinaryExpression(val left: Expression, val right: Expression, val operator: BinaryOperator) : Expression()

enum class BinaryOperator : java.util.function.BinaryOperator<Int> {
    MULT {
        override fun apply(l: Int, r: Int): Int = l * r
    },
    DIV {
        override fun apply(l: Int, r: Int): Int = l / r
    },
    MOD {
        override fun apply(l: Int, r: Int): Int = l % r
    },
    PLUS {
        override fun apply(l: Int, r: Int): Int = l + r
    },
    MINUS {
        override fun apply(l: Int, r: Int): Int = l - r
    },
    LE {
        override fun apply(l: Int, r: Int): Int = toInt(l <= r)
    },
    GE {
        override fun apply(l: Int, r: Int): Int = toInt(l >= r)
    },
    LT {
        override fun apply(l: Int, r: Int): Int = toInt(l < r)
    },
    GT {
        override fun apply(l: Int, r: Int): Int = toInt(l > r)
    },
    EQ {
        override fun apply(l: Int, r: Int): Int = toInt(l == r)
    },
    NEQ {
        override fun apply(l: Int, r: Int): Int = toInt(l != r)
    },
    AND {
        override fun apply(l: Int, r: Int): Int = toInt(toBool(l) && toBool(r))
    },
    OR {
        override fun apply(l: Int, r: Int): Int = toInt(toBool(l) || toBool(r))
    }
}

fun toInt(x: Boolean): Int = if (x) 1 else 0

fun toBool(x: Int): Boolean = x != 0

data class Identifier(val identifier: String) : Expression()

data class Literal(val literal: Int) : Expression()

