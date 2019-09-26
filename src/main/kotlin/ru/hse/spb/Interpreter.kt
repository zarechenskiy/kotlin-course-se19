package ru.hse.spb

import ru.hse.spb.declarations.AssignmentStatement
import ru.hse.spb.declarations.BinaryExpression
import ru.hse.spb.declarations.BinaryOperator.*
import ru.hse.spb.declarations.BuiltinFunctionStatement
import ru.hse.spb.declarations.CustomFunctionStatement
import ru.hse.spb.declarations.CycleStatement
import ru.hse.spb.declarations.ExpressionDeclaration
import ru.hse.spb.declarations.FunctionCallExpression
import ru.hse.spb.declarations.IfStatement
import ru.hse.spb.declarations.NumberExpression
import ru.hse.spb.declarations.ReturnStatement
import ru.hse.spb.declarations.StatementDeclaration
import ru.hse.spb.declarations.VariableExpression
import ru.hse.spb.declarations.VariableStatement

fun runBlock(body: List<StatementDeclaration>, scope: Scope): Int? {
    for (statement in body) {
        val result = runStatement(statement, scope)
        result?.let { return result }
    }
    return null
}

fun runStatement(statement: StatementDeclaration, scope: Scope): Int? {
    when (statement) {
        is CustomFunctionStatement -> {
            scope.registerFunction(statement.name, statement)
        }
        is VariableStatement -> {
            scope.registerVariable(
                statement.name,
                statement.initializer?.let { evaluateExpression(it, scope) }
            )
        }
        is ExpressionDeclaration -> {
            evaluateExpression(statement, scope)
        }
        is CycleStatement -> {
            while (evaluateExpression(statement.condition, scope).toBoolean()) {
                runBlock(statement.body, scope.clone())?.let { return it }
            }
        }
        is IfStatement -> {
            if (evaluateExpression(statement.condition, scope).toBoolean()) {
                runBlock(statement.mainBranch, scope.clone())?.let { return it }
            } else {
                statement.elseBranch?.let { runBlock(it, scope.clone()) }?.let { return it }
            }
        }
        is AssignmentStatement -> {
            scope.updateVariable(
                statement.name,
                evaluateExpression(statement.value, scope)
            )
        }
        is ReturnStatement -> {
            return evaluateExpression(statement.expression, scope)
        }
    }
    return null
}

fun callFunction(expression: FunctionCallExpression, scope: Scope): Int {
    when (val function = scope.getFunction(expression.name)) {
        is BuiltinFunctionStatement -> {
            function.body(
                expression.arguments.map { evaluateExpression(it, scope) }
            )
        }
        is CustomFunctionStatement -> {
            val childScope = scope.clone()
            function.arguments.zip(expression.arguments)
                .map { (name, expression): Pair<String, ExpressionDeclaration> ->
                    childScope.registerVariable(name, evaluateExpression(expression, scope))
                }
            return runBlock(function.body, childScope) ?: 0
        }
    }
    return 0
}

fun evaluateExpression(expression: ExpressionDeclaration, scope: Scope): Int {
    return when (expression) {
        is NumberExpression -> expression.number
        is BinaryExpression -> evaluateBinaryExpression(expression, scope)
        is VariableExpression -> scope.getVariable(expression.name)
        is FunctionCallExpression -> callFunction(expression, scope)
    }
}

private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

private fun Int.toBoolean(): Boolean {
    return this != 0
}

private fun evaluateBinaryExpression(expression: BinaryExpression, scope: Scope): Int {
    val leftResult = evaluateExpression(expression.lhs, scope)
    val rightResult = evaluateExpression(expression.rhs, scope)
    return when (expression.op) {
        MUL -> leftResult * rightResult
        DIV -> leftResult / rightResult
        MOD -> leftResult % rightResult
        PLUS -> leftResult + rightResult
        MINUS -> leftResult - rightResult
        LESS -> (leftResult < rightResult).toInt()
        GREATER -> (leftResult > rightResult).toInt()
        LESS_EQUALS -> (leftResult <= rightResult).toInt()
        GREATER_EQUALS -> (leftResult >= rightResult).toInt()
        EQUALS -> (leftResult == rightResult).toInt()
        NOT_EQUALS -> (leftResult != rightResult).toInt()
        AND -> (leftResult.toBoolean() && rightResult.toBoolean()).toInt()
        OR -> (leftResult.toBoolean() || rightResult.toBoolean()).toInt()
    }
}
