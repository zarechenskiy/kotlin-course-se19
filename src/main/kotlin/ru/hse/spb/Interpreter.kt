package ru.hse.spb

import java.lang.RuntimeException

fun evaluate(block: Block, state: State): Int? {
    for (statement in block.body) {
        val result = evaluate(statement, state)
        if (result != null) {
            return result
        }
    }
    return null
}

fun evaluate(statement: Statement, state: State): Int? {
    when (statement) {
        is Function -> {
            state.setFunction(statement.identifier, statement)
            return null
        }
        is Variable -> {
            if (statement.value != null) {
                state.setVariable(statement.identifier, evaluate(statement.value, state))
            } else {
                state.setVariable(statement.identifier, null)
            }
            return null
        }
        is While -> {
            while (toBool(evaluate(statement.condition, state))) {
                val result = evaluate(statement.body, state.enter())
                if (result != null) {
                    return result
                }
            }
        }
        is If -> {
            if (toBool(evaluate(statement.condition, state))) {
                val result = evaluate(statement.thenBody, state.enter())
                if (result != null) {
                    return result
                }
            } else {
                val result = evaluate(statement.elseBody, state.enter())
                if (result != null) {
                    return result
                }
            }
        }
        is Assignment -> {
            state.setVariable(statement.identifier, evaluate(statement.expression, state))
            return null
        }
        is Return -> {
            return evaluate(statement.expression, state)
        }
    }
    return null
}

fun evaluate(expression: Expression, state: State): Int {
    when (expression) {
        is FunctionCall -> {
            if (expression.identifier == "println") {
                println(expression.parameters)
                return 0
            } else {
                val function = state.getFunction(expression.identifier)
                if (function.parameters.size != expression.parameters.size) {
                    throw RuntimeException("Wrong number of arguments in function ${function.identifier} call")
                }
                val newState = state.enter()
                function.parameters.zip(expression.parameters).map { (param, expr) -> newState.setVariable(param, evaluate(expr, state))}
                return evaluate(function.body, newState) ?: 0
            }
        }
        is BinaryExpression -> {
            return expression.operator.apply(evaluate(expression.left, state), evaluate(expression.right, state))
        }
        is Identifier -> {
            return state.getVariable(expression.identifier)
        }
        is Literal -> {
            return expression.literal
        }
    }
}

