package ru.hse.spb

import java.lang.RuntimeException

class State(private val parent: State?) {
    private val variables: MutableMap<String, Int?> = mutableMapOf()
    private val functions: MutableMap<String, Function> = mutableMapOf()

    fun registerVariable(identifier: String, value: Int?) {
        if (identifier in variables) {
            throw RuntimeException("Multiple definition of variable $identifier")
        } else {
            variables[identifier] = value;
        }
    }

    fun setVariable(identifier: String, value: Int) {
        when {
            identifier in variables -> variables[identifier] = value
            parent != null -> parent.setVariable(identifier, value)
            else -> throw RuntimeException("Unknown variable $identifier")
        }
    }

    fun getVariable(identifier: String): Int =
            when {
                identifier in variables && variables[identifier] != null ->
                    variables[identifier]!!
                identifier in variables ->
                    throw RuntimeException("Variable $identifier is not initialized")
                parent != null -> parent.getVariable(identifier)
                else -> throw RuntimeException("Unknown variable $identifier")
            }

    fun registerFunction(identifier: String, function: Function) {
        functions[identifier] = function
    }

    fun getFunction(identifier: String): Function =
            when {
                identifier in functions -> functions[identifier]!!
                parent != null -> parent.getFunction(identifier)
                else -> throw RuntimeException("Unknown function $identifier")
            }

    fun enter(): State = State(this)
}