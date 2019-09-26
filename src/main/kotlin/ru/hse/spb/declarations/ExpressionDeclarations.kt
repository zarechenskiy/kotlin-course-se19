package ru.hse.spb.declarations

sealed class ExpressionDeclaration : StatementDeclaration

data class BinaryExpression(
    val lhs: ExpressionDeclaration,
    val rhs: ExpressionDeclaration,
    val op: BinaryOperator
) : ExpressionDeclaration()

enum class BinaryOperator {
    MUL,
    DIV,
    MOD,
    PLUS,
    MINUS,
    LESS,
    GREATER,
    LESS_EQUALS,
    GREATER_EQUALS,
    EQUALS,
    NOT_EQUALS,
    AND,
    OR
}

data class FunctionCallExpression(
    val name: String,
    val arguments: List<ExpressionDeclaration>
) : ExpressionDeclaration()

data class NumberExpression(
    val number: Int
) : ExpressionDeclaration()

data class VariableExpression(
    val name: String
) : ExpressionDeclaration()