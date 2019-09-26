package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

/**
 * Visitor for evaluating expression
 * */
class ExpressionVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    /**
     * Evaluates expression
     *
     * @return the result of evaluating accroding to the current address scope and math rule of evaluating
     * */
    override fun visitExpression(ctx: LangParser.ExpressionContext?): Int {
        if (ctx?.LPAREN() != null) {
            check(ctx.middle != null) { "Parsing error at line ${ctx.start.line}" }
            return ctx.middle.accept(this)
        }

        if (ctx?.IDENTIFIER()?.text != null) {
            val address = context.varAddresses[ctx.IDENTIFIER().text]
            if (address != null && context.varValues.containsKey(address)) {
                return context.varValues[address]!!
            } else {
                throw IllegalStateException("Can not use undefined variable: ${ctx.IDENTIFIER().text}")
            }
        }

        if (ctx?.LITERAL() != null) {
            return ctx.LITERAL().text.toInt()
        }

        if (ctx?.operator != null && ctx.left != null && ctx.right != null) {
            val leftValue = ctx.left.accept(this)
            val rightValue = ctx.right.accept(this)

            fun Boolean.toInt() = if (this) 1 else 0

            return when (ctx.operator.type) {
                LangParser.ASTERISK -> leftValue * rightValue
                LangParser.DIVISION -> leftValue / rightValue
                LangParser.REMINDER -> leftValue % rightValue
                LangParser.MINUS    -> leftValue - rightValue
                LangParser.PLUS     -> leftValue + rightValue
                LangParser.LT       -> (leftValue < rightValue).toInt()
                LangParser.GT       -> (leftValue > rightValue).toInt()
                LangParser.LE       -> (leftValue <= rightValue).toInt()
                LangParser.GE       -> (leftValue >= rightValue).toInt()
                LangParser.EQ       -> (leftValue == rightValue).toInt()
                LangParser.NEQ      -> (leftValue != rightValue).toInt()
                LangParser.AND      -> (leftValue != 0 && rightValue != 0).toInt()
                LangParser.OR       -> (leftValue != 0 || rightValue != 0).toInt()
                else -> {
                    throw IllegalArgumentException(
                        "Illegal operator was found: " + LangParser.VOCABULARY.getSymbolicName(ctx.operator.type)
                    )
                }
            }
        }

        if (ctx?.function_call() != null) {
            return ctx.function_call().accept(FunctionCallVisitor(context.copy()))
        }

        throw IllegalStateException("Parsing error at line ${ctx?.getStart()?.line}")
    }
}
