package ru.hse.spb.interpreter

import ru.hse.spb.parser.ProgrammeParser.FunctionContext

data class Scope(val parentScope: Scope?) {

    var variableTable = mutableMapOf<String, Int>()
    var functionTable = mutableMapOf<String, FunctionContext>()

    fun addFunction(name: String, ctx: FunctionContext) {
        functionTable[name] = ctx
    }

    fun addVariable(name: String, value: Int) {
        variableTable[name] = value
    }

    fun addVariables(varNames: List<String>, varValues: List<Int>) {
        for ( (name, value) in varNames zip varValues) {
            addVariable(name, value)
        }
    }

    fun getFunction(name: String): Pair<FunctionContext, Scope>? {
        if (functionTable.contains(name)) {
            return Pair(functionTable[name]!!, this)
        }
        return parentScope?.getFunction(name)
    }

    fun getVariable(name: String): Int? {
        if (variableTable.contains(name)) {
            return variableTable[name]!!
        }
        return parentScope?.getVariable(name)
    }

    fun recursiveContainsVariable(name: String): Boolean {
        return when (parentScope) {
            null -> containsLocalVariable(name)
            else -> containsLocalVariable(name) || parentScope.recursiveContainsVariable(name)
        }
    }

    fun recursiveContainsFunction(name: String): Boolean {
        if (parentScope == null) {
            return containsLocalFunction(name)
        }
        return containsLocalFunction(name) || parentScope.recursiveContainsFunction(name)
    }

    fun containsLocalVariable(name: String): Boolean {
        return variableTable.containsKey(name)
    }

    fun containsLocalFunction(name: String): Boolean {
        return functionTable.containsKey(name)
    }

    fun setVariable(name: String, value: Int) {
        if (variableTable.contains(name)) {
            variableTable[name] = value
            return
        }
        parentScope!!.setVariable(name, value)
    }
}
