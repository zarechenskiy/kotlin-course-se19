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
