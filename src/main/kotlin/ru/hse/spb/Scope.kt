package ru.hse.spb

import ru.hse.spb.declarations.BuiltinFunctionStatement
import ru.hse.spb.declarations.FunctionStatement

class Scope(private val parent: Scope?) {
    private val uninitializedVariables: MutableSet<String> = mutableSetOf()
    private val variables: MutableMap<String, Int> = mutableMapOf()
    private val functions: MutableMap<String, FunctionStatement> = mutableMapOf()

    init {
        if (parent == null) {
            functions["println"] = BuiltinFunctionStatement { args ->
                println(args.joinToString(separator = " "))
            }
        }
    }

    fun getVariable(name: String): Int {
        if (name in uninitializedVariables) {
            throw RuntimeException("Variable $name is not initialized")
        }
        return variables[name]
            ?: parent?.getVariable(name)
            ?: throw RuntimeException("Unknown variable $name")
    }

    fun registerVariable(name: String, value: Int?) {
        if (name in variables || name in uninitializedVariables) {
            throw java.lang.RuntimeException("Variable $name is already declared")
        }
        if (value == null) {
            uninitializedVariables += name
        } else {
            variables[name] = value
        }
    }

    fun updateVariable(name: String, value: Int) {
        when {
            name in uninitializedVariables -> {
                variables[name] = value
                uninitializedVariables -= name
            }
            name in variables -> variables[name] = value
            parent != null -> parent.updateVariable(name, value)
            else -> throw RuntimeException("Unknown variable $name")
        }
    }

    fun getFunction(name: String): FunctionStatement {
        return functions[name]
            ?: parent?.getFunction(name)
            ?: throw RuntimeException("Unknown function $name")
    }

    fun registerFunction(name: String, function: FunctionStatement) {
        functions[name] = function
    }

    fun clone(): Scope {
        return Scope(this)
    }
}