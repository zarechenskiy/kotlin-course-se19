package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext

typealias TFunction = (List<Int?>) -> Int?

class Scope(private val parentScope: Scope? = null) {
    private val variables = mutableMapOf<String, Int?>()
    private val functions = mutableMapOf<String, TFunction>()

    fun getVariable(identifier: String, ctx: ParserRuleContext): Int? {
        return variables[identifier] ?:
        parentScope?.getVariable(identifier, ctx) ?:
        throw RuntimeException("Line ${ctx.start.line}: variable referenced before assignment.")
    }

    fun getFunction(identifier: String, ctx: ParserRuleContext): TFunction {
        return functions[identifier] ?:
        parentScope?.getFunction(identifier, ctx) ?:
        throw RuntimeException("Line ${ctx.start.line}: unexpected function call.")
    }

    fun updateVariable(identifier: String, value: Int?, ctx: ParserRuleContext) {
        if (variables.containsKey(identifier)) {
            variables[identifier] = value
        } else {
            parentScope?.updateVariable(identifier, value, ctx) ?:
            throw RuntimeException("Line ${ctx.start.line}: variable not in scope.")
        }
    }

    fun addVariable(identifier: String, value: Int?, ctx: ParserRuleContext) {
        if (identifier in variables) {
            throw RuntimeException("Line ${ctx.start.line}: duplicate variable definition.")
        }

        variables[identifier] = value
    }

    fun addFunction(identifier: String, function: TFunction, ctx: ParserRuleContext) {
        if (functions.containsKey(identifier)) {
            throw RuntimeException("Line ${ctx.start.line}: duplicate function definition.")
        }

        functions[identifier] = function
    }
}