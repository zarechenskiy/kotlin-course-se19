package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class BinaryExpressionVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitBinary_expression(ctx: LangParser.Binary_expressionContext?): Int {
        if (ctx?.LPAREN() != null) {
            check(ctx.middle != null) { "Parse error at line ${ctx.start.line}" }
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
            return when (ctx.operator.type) {
                LangParser.ASTERISK -> leftValue * rightValue
                LangParser.DIVISION -> leftValue / rightValue
                LangParser.REMINDER -> leftValue % rightValue
                LangParser.MINUS    -> leftValue - rightValue
                LangParser.PLUS     -> leftValue + rightValue
                else -> {
                    throw IllegalArgumentException("Illegal operator was found: " +
                            LangParser.VOCABULARY.getSymbolicName(ctx.operator.type)
                    )
                }
            }
        }
        throw IllegalStateException("Parsing error at line ${ctx?.getStart()?.line}")
    }
}
