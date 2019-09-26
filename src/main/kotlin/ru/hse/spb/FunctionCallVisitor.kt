package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser
import java.lang.IllegalStateException

class FunctionCallVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    private fun visitPrintln(ctx: LangParser.Function_callContext): Int {
        val args = ctx.arguments().accept(ArgumentsVisitor(context.copy()))
        for (i in args.indices) {
            if (i > 0) {
                print(' ')
            }
            print(args[i])
        }
        println()
        return 0
    }

    override fun visitFunction_call(ctx: LangParser.Function_callContext?): Int {
        val functionName = ctx!!.IDENTIFIER()!!.text

        if (functionName == "println") {
            return visitPrintln(ctx)
        }

        if (!context.definedFunctions.containsKey(functionName)) {
            throw IllegalStateException("Can not call undefined function: ${ctx.IDENTIFIER().text}")
        }

        val functionCtx = context.definedFunctions[functionName]

        if (functionCtx?.parameter_names()?.IDENTIFIER() == null) {
            throw IllegalStateException("Parsing error at line ${functionCtx?.start?.line}")
        }

        val expectedArgsNumber = functionCtx.parameter_names().IDENTIFIER().size
        val foundArgsNumber = if (ctx.arguments()?.expression() != null) ctx.arguments().expression().size else 0

        if (expectedArgsNumber != foundArgsNumber) {
            throw IllegalStateException("Incorrect number of arguments: expected " +
                    "$expectedArgsNumber" +
                    ", but found " +
                    "$foundArgsNumber")
        }

        if (functionCtx.block_with_braces()?.block() == null) {
            return 0
        }

        val nextContext = context.copy()

        if (expectedArgsNumber > 0) {
            val args = ctx.arguments().accept(ArgumentsVisitor(nextContext))

            if (args != null && args.isNotEmpty()) {
                val names = functionCtx.parameter_names().IDENTIFIER().map { it.text }
                for (i in args.indices) {
                    nextContext.varValues[names[i]] = args[i]
                }
            }

        }

        functionCtx.block_with_braces().block().accept(BlockVisitor(nextContext))

        return nextContext.resultValue ?: 0
    }
}
