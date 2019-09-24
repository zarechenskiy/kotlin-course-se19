package ru.hse.spb.funterpreter

import ru.hse.spb.parser.FunGrammarParser

sealed class Value
data class IntValue(val value : Int = 0) : Value()
class Function(val parentScope: ValueScope, ctx: FunGrammarParser.FunctionContext) : Value() {
    val body: FunGrammarParser.Block_with_bracesContext = ctx.block_with_braces()
    val argumentNames = ctx.parameter_names().identifier().map { it.IDENTIFIER().symbol.text }
    val argumentCount
        get() = argumentNames.size

    override fun toString(): String {
        return argumentNames.joinToString(prefix = "<function>(", postfix = ")")
    }
}