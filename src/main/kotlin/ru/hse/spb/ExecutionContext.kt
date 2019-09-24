package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext

data class ExecutionContext(
    val varValues: Map<String, Int> = HashMap(),
    val definedFunctions: Map<String, ParserRuleContext> = HashMap()
) {
    fun getVariableValue(name: String): Int? {
        return varValues[name]
    }
}
