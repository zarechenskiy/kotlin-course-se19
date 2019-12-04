package ru.hse.spb

/* block: (statement)* */
data class Block(val body: List<Statement>)

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

enum class BinaryOperator(private val operation: (Int, Int) -> Int) : (Int, Int) -> Int  {
    MULT  ({l, r -> l * r}),
    DIV   ({l, r -> l / r}),
    MOD   ({l, r -> l % r}),
    PLUS  ({l, r -> l + r}),
    MINUS ({l, r -> l - r}),
    LE    ({l, r -> toInt(l <= r)}),
    GE    ({l, r -> toInt(l >= r)}),
    LT    ({l, r -> toInt(l  < r)}),
    GT    ({l, r -> toInt(l  > r)}),
    EQ    ({l, r -> toInt(l == r)}),
    NEQ   ({l, r -> toInt(l != r)}),
    AND   ({l, r -> toInt(toBool(l) && toBool(r)) }),
    OR    ({l, r -> toInt(toBool(l) || toBool(r)) });

    override fun invoke(l: Int, r: Int): Int = operation(l, r)
}

fun toInt(x: Boolean): Int = if (x) 1 else 0

fun toBool(x: Int): Boolean = x != 0

data class Identifier(val identifier: String) : Expression()

data class Literal(val literal: Int) : Expression()

