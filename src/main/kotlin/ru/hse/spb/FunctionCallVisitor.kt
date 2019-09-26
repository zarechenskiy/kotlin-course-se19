package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser
import java.lang.IllegalStateException

/**
 * Visitor for processing of functions calling
 * */
class FunctionCallVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    /**
     * Handles the call of builtin function println and
     * adds lines into the standard out which is stored in address space
     */
    private fun visitPrintln(ctx: LangParser.Function_callContext): Int {
        val args = ctx.arguments().accept(ArgumentsVisitor(context.copy()))
        ExecutionContext.stdOut.add(args.joinToString(separator = " "))
        return 0
    }

    /**
     * Checks all conditions including existing of function and
     * the number of arguments and evaluate the value of function calling
     *
     * @return the result of function calling
     * */
    override fun visitFunction_call(ctx: LangParser.Function_callContext?): Int {
        val functionName = ctx!!.IDENTIFIER()!!.text

        if (functionName == "println") {
            return visitPrintln(ctx)
        }

        check(context.funAddresses.containsKey(functionName)) {
            "Can not call undefined function: ${ctx.IDENTIFIER().text}"
        }

        val functionAddress = context.funAddresses[functionName]
        val functionCtx = context.funDefinition[functionAddress]

        checkNotNull(functionCtx?.parameter_names()?.IDENTIFIER()) {
            "Parsing error at line ${functionCtx?.start?.line}"
        }

        val expectedArgsNumber = functionCtx!!.parameter_names().IDENTIFIER().size
        val foundArgsNumber = ctx.arguments()?.expression()?.size ?: 0

        check(expectedArgsNumber == foundArgsNumber) {
            "Incorrect number of arguments: expected " +
            "$expectedArgsNumber" +
            ", but found " +
            "$foundArgsNumber"
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
                    nextContext.addVariable(names[i], args[i])
                }
            } else {
                throw IllegalStateException(
                    "Parsing error at line ${ctx.start.line}: " +
                    "can not evaluate arguments for function defined at line ${functionCtx.start.line}"
                )
            }
        }

        functionCtx.block_with_braces().block().accept(BlockVisitor(nextContext))

        return nextContext.resultValue ?: 0
    }
}
