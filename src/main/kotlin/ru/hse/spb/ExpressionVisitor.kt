package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class ExpressionVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitExpression(ctx: LangParser.ExpressionContext?): Int {

        if (ctx?.LPAREN() != null) {
            check(ctx.middle != null) { "Parsing error at line ${ctx.start.line}" }
            return ctx.middle.accept(this)
        }

        if (ctx?.IDENTIFIER() != null) {
            val value = context.getVariableValue(ctx.IDENTIFIER().text)
            if (value != null) {
                return value
            } else {
                throw IllegalStateException("Can not use undefined varible: ${ctx.IDENTIFIER()}")
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
