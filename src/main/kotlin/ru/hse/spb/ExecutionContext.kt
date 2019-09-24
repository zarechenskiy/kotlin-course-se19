package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.LangParser

data class ExecutionContext(
    val varValues: Map<String, Int> = HashMap(),
    val definedFunctions: Map<String, LangParser.FunctionContext> = HashMap()
) {
    fun getVariableValue(name: String): Int? {
        return varValues[name]
    }
}
