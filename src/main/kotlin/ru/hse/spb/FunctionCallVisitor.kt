package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser
import java.lang.IllegalStateException

class FunctionCallVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitFunction_call(ctx: LangParser.Function_callContext?): Int? {
        if (!context.definedFunctions.containsKey(ctx?.IDENTIFIER()!!.text)) {
            throw IllegalStateException("Can not recognize undefined function: ${ctx.IDENTIFIER().text}")
        }

        val functionCtx = context.definedFunctions[ctx.IDENTIFIER().text]

        if (functionCtx?.parameter_names()?.IDENTIFIER() == null) {
            throw IllegalStateException("Parsing error at line ${functionCtx?.start?.line}")
        }

        if (functionCtx.parameter_names().IDENTIFIER().size != ctx.arguments().expression().size) {
            throw IllegalStateException("Incorrect number of arguments: exptected " +
                    "${functionCtx.parameter_names().IDENTIFIER().size}" +
                    ", but found " +
                    "${ctx.arguments().expression().size}")
        }

        val args = ctx.arguments().accept(ArgumentsVisitor(context.copy()))
        return functionCtx.block_with_braces()?.block()?.accept(BlockVisitor(context.copy()))
    }
}
