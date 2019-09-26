package ru.hse.spb

import ru.hse.spb.parser.FunParser

class Function(val scope: Int,
               private val argumentNames: List<String>,
               private val ctx: FunParser.BlockWithBracesContext,
               private val visitor: StatementsEvaluationVisitor) {
    fun invoke(state: State, arguments: List<Int>): Int {
        state.newScope()

        if (arguments.size != argumentNames.size) {
            throw IllegalFunctionCall("Wrong number of arguments")
        }
        for (argumentId in 0..arguments.lastIndex) {
            state.addVariable(argumentNames[argumentId], arguments[argumentId])
        }

        val returnValue = ctx.block().accept(visitor)

        state.leaveScope()
        return returnValue ?: 0
    }
}