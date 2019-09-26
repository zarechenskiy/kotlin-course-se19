package ru.hse.spb

import ru.hse.spb.parser.LangParser

data class ExecutionContext(
    val varValues: MutableMap<String, Int> = HashMap(),
    val definedFunctions: MutableMap<String, LangParser.FunctionContext> = HashMap(),
    var resultValue: Int? = null
) {
    fun getVariableValue(name: String): Int? {
        return varValues[name]
    }

    fun copy(): ExecutionContext = ExecutionContext(varValues.copy(), definedFunctions.copy(), resultValue)
}

fun <U, V> MutableMap<U, V>.copy(): MutableMap<U, V> {
    val to: MutableMap<U, V> = HashMap()
    for (k in this.keys) {
        to[k] = this[k]!!
    }
    return to
}


