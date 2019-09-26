package ru.hse.spb

class State {
    val scopes = mutableListOf<Scope>()

    fun newScope() {
        scopes.add(Scope())
    }

    fun leaveScope() {
        scopes.removeAt(scopes.lastIndex)
    }

    fun addFunction(functionName: String, function: Function) {
        scopes[scopes.lastIndex].functions[functionName] = function
    }

    fun addVariable(variableName: String, value: Int) {
        scopes[scopes.lastIndex].variables[variableName] = value
    }

    private fun getFunction(functionName: String): Function {
        for (scopeId in scopes.lastIndex downTo 0) {
            if (scopes[scopeId].functions[functionName] != null) {
                return scopes[scopeId].functions[functionName]!!
            }
        }
        throw NoSuchFunctionException(functionName)
    }

    fun setVariable(variableName: String, newValue: Int) {
        for (scopeId in scopes.lastIndex downTo 0) {
            if (scopes[scopeId].variables[variableName] != null) {
                scopes[scopeId].variables[variableName] = newValue
                return
            }
        }
        throw NoSuchVariableException(variableName)
    }

    fun getVariable(variableName: String): Int {
        for (scopeId in scopes.lastIndex downTo 0) {
            if (scopes[scopeId].variables[variableName] != null) {
                return scopes[scopeId].variables[variableName]!!
            }
        }
        throw NoSuchVariableException(variableName)
    }

    fun invokeFunction(functionName: String, arguments: List<Int>): Int {
        val function = getFunction(functionName)
        val removedScopes = mutableListOf<Scope>()
        for (scopeId in (function.scope + 1)..scopes.lastIndex) {
            removedScopes.add(scopes[scopeId])
        }
        while (scopes.lastIndex != function.scope) {
            scopes.removeAt(scopes.lastIndex)
        }

        val returnValue = function.invoke(this, arguments)

        scopes.addAll(removedScopes)
        return returnValue
    }
}

class Scope {
    val functions = mutableMapOf<String, Function>()
    val variables = mutableMapOf<String, Int>()
}