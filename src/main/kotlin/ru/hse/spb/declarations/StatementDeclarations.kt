package ru.hse.spb.declarations


interface StatementDeclaration

interface FunctionStatement : StatementDeclaration

data class CustomFunctionStatement(
    val name: String,
    val arguments: List<String>,
    val body: List<StatementDeclaration>
) : FunctionStatement

data class BuiltinFunctionStatement(
    val body: (List<Int>) -> Unit
) : FunctionStatement

data class VariableStatement(
    val name: String,
    val initializer: ExpressionDeclaration?
) : StatementDeclaration

data class CycleStatement(
    val condition: ExpressionDeclaration,
    val body: List<StatementDeclaration>
) : StatementDeclaration

data class IfStatement(
    val condition: ExpressionDeclaration,
    val mainBranch: List<StatementDeclaration>,
    val elseBranch: List<StatementDeclaration>?
) : StatementDeclaration

data class AssignmentStatement(
    val name: String,
    val value: ExpressionDeclaration
) : StatementDeclaration

data class ReturnStatement(
    val expression: ExpressionDeclaration
) : StatementDeclaration
