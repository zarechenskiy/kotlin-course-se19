package ru.hse.spb

import ru.hse.spb.parser.GrammarParser

/**
 * Scope of file contains functions and variables between curly braces.
 */
class Scope(val parent: Scope? = null) {

    /** Variables in scope, null if no value has been specified. */
    private var variables: HashMap<String, Int?> = HashMap()

    /** Functions in scope. */
    private var functions: HashMap<String, FunctionScope> = HashMap()

    /** Check if variable is available. */
    fun containsVariable(name: String): Boolean {
        return variables.containsKey(name) || parent?.containsVariable(name) ?: false
    }

    /** Check if function is available. */
    fun containsFunction(name: String): Boolean {
        return functions.containsKey(name) || parent?.containsFunction(name) ?: false
    }

    /** Check if variable is available in current scope without parents. */
    fun containsVariableInCurrentScope(name: String) = variables.containsKey(name)

    /** Check if function is available in current scope without parents. */
    fun containsFunctionInCurrentScope(name: String) = functions.containsKey(name)

    /** Return variable value by name. */
    fun getVariable(name: String): Int? = variables[name] ?: parent?.getVariable(name)

    /** Return function by name. */
    fun getFunction(name: String): FunctionScope? = functions[name] ?: parent?.getFunction(name)

    /** Add variable in scope. */
    fun putVariable(name: String, value: Int?) {
        variables[name] = value
    }

    /** Add function in scope. */
    fun putFunction(name: String, value: FunctionScope) {
        functions[name] = value
    }

    /** Set variable value. Variable should be defined before. */
    fun setVariable(name: String, value: Int) {
        require(containsVariable(name))
        if (!containsVariableInCurrentScope(name)) {
            parent!!.setVariable(name, value)
            return
        }
        variables[name] = value
    }

}

/** Function scope contains function context to be interpreted and function definition context. */
data class FunctionScope(val function: GrammarParser.FunctionContext, val scope: Scope)