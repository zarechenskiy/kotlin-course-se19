package ru.hse.spb.funterpreter

import ru.hse.spb.parser.FunGrammarBaseVisitor
import ru.hse.spb.parser.FunGrammarParser

sealed class Value
data class IntValue(val value : Int = 0) : Value()
class Function(val parentScope: ValueScope, ctx: FunGrammarParser.FunctionContext) : Value() {
    val body = ctx.block_with_braces()
    val argumentNames : List<String>
            = ctx.parameter_names().identifier().map { it.IDENTIFIER().symbol.text }
    val argumentCount
        get() = argumentNames.size
}