package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser
import java.lang.IllegalStateException

class StatementVisitor() : LangBaseVisitor<Int?>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext) : this() {
        this.context = context
    }

    override fun visitStatement(ctx: LangParser.StatementContext?): Int? {
        if (ctx == null) {
            return null
        }
        when {
            ctx.function() != null -> ctx.function().accept(this)
            ctx.assignment() != null -> ctx.assignment().accept(this)
            ctx.expression() != null -> ctx.expression().accept(this)
            ctx.variable() != null -> ctx.variable().accept(this)
            ctx.while_expr() != null -> ctx.while_expr().accept(this)
            ctx.if_expr() != null -> ctx.if_expr().accept(this)
            ctx.return_expr() != null -> return ctx.return_expr()
                ?.expression()
                ?.accept(ExpressionVisitor(context))
        }
        return null
    }

    override fun visitFunction(ctx: LangParser.FunctionContext?): Int? {
        val functionName = ctx!!.FUNCTION()!!.text!!
        if (context.definedFunctions.containsKey(functionName)) {
            throw IllegalStateException(
                "Multiply definition of function ${functionName} " +
                        "at line ${ctx.start.line}"
            )
        }
        context.definedFunctions[functionName] = ctx
        return null
    }

    override fun visitAssignment(ctx: LangParser.AssignmentContext?): Int? {
        if (ctx?.IDENTIFIER() == null || ctx.expression() == null) {
            throw IllegalStateException("Parsing error at line: ${ctx?.start?.line}")
        }
        if (!context.varValues.containsKey(ctx.IDENTIFIER().text)) {
            throw IllegalStateException("Can not set undfined variable: ${ctx.IDENTIFIER().text}")
        }
        context.varValues[ctx.IDENTIFIER().text] = ctx.expression().accept(ExpressionVisitor(context))
        return null
    }

    override fun visitWhile_expr(ctx: LangParser.While_exprContext?): Int? {
        while (true) {
            val conditionValue = ctx!!.expression().accept(ExpressionVisitor(context))
            if (conditionValue == 0) {
                break
            }
            val blockResult = ctx.block_with_braces()!!.block().accept(BlockVisitor(context.copy()))
            if (blockResult != null) {
                return blockResult
            }
        }
        return null
    }

    override fun visitIf_expr(ctx: LangParser.If_exprContext?): Int? {
        val conditionValue = ctx!!.expression().accept(ExpressionVisitor(context))
        if (conditionValue == 1) {
            val blockResult = ctx.block_with_braces()[0].accept(BlockVisitor(context))
            if (blockResult != null) {
                return blockResult
            }
        } else if (ctx.block_with_braces().size == 2) {
            val blockResult = ctx.block_with_braces()[1].accept(BlockVisitor(context))
            if (blockResult != null) {
                return blockResult
            }
        }
        return null
    }
}
