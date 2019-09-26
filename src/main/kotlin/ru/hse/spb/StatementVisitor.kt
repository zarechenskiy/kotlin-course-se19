package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class StatementVisitor() : LangBaseVisitor<Void?>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext) : this() {
        this.context = context
    }

    override fun visitStatement(ctx: LangParser.StatementContext?): Void? {
        if (ctx == null) {
            return null
        }

        when {
            ctx.return_expr() != null -> {
                context.resultValue = ctx.return_expr()
                    ?.expression()
                    ?.accept(ExpressionVisitor(context))
            }
            ctx.function() != null -> ctx.function().accept(this)
            ctx.assignment() != null -> ctx.assignment().accept(this)
            ctx.expression() != null -> ctx.expression().accept(ExpressionVisitor(context))
            ctx.variable() != null -> ctx.variable().accept(this)
            ctx.while_expr() != null -> ctx.while_expr().accept(this)
            ctx.if_expr() != null -> ctx.if_expr().accept(this)
        }

        return null
    }

    override fun visitFunction(ctx: LangParser.FunctionContext?): Void? {
        val functionName = ctx!!.IDENTIFIER()!!.text!!
        check(!(context.definedFunctions.containsKey(functionName) || functionName == "println")) {
            "Multiply definition of function $functionName " +
            "at line ${ctx.start.line}"
        }
        context.definedFunctions[functionName] = ctx
        return null
    }

    override fun visitAssignment(ctx: LangParser.AssignmentContext?): Void? {
        check(!(ctx?.IDENTIFIER() == null || ctx.expression() == null)) {
            "Parsing error at line: ${ctx?.start?.line}"
        }
        check(context.varValues.containsKey(ctx!!.IDENTIFIER().text)) {
            "Can not set value to the undefined variable: ${ctx.IDENTIFIER().text}"
        }
        context.varValues[ctx.IDENTIFIER().text] = ctx.expression().accept(ExpressionVisitor(context))
        return null
    }

    override fun visitWhile_expr(ctx: LangParser.While_exprContext?): Void? {
        while (true) {
            val conditionValue = ctx!!.expression().accept(ExpressionVisitor(context))
            if (conditionValue == 0) {
                break
            }
            val nextContext = context.copy()
            ctx.block_with_braces()!!.block().accept(BlockVisitor(nextContext))
            context.updateVariables(nextContext)
            if (nextContext.resultValue != null) {
                context.resultValue = nextContext.resultValue
                break
            }
        }
        return null
    }

    override fun visitIf_expr(ctx: LangParser.If_exprContext?): Void? {
        val conditionValue = ctx!!.expression().accept(ExpressionVisitor(context))
        val nextContext = context.copy()
        if (conditionValue == 1) {
            ctx.block_with_braces()[0].block().accept(BlockVisitor(nextContext))
        } else if (ctx.block_with_braces().size == 2) {
            ctx.block_with_braces()[1].block().accept(BlockVisitor(context.copy()))
        }
        if (nextContext.resultValue != null) {
            context.resultValue = nextContext.resultValue
        }
        context.updateVariables(nextContext)
        return null
    }

    override fun visitVariable(ctx: LangParser.VariableContext?): Void? {
        val name = ctx!!.IDENTIFIER().text
        var value = 0
        if (ctx.expression() != null) {
            value = ctx.expression().accept(ExpressionVisitor(context))
        }
        context.varValues[name] = value
        return null
    }
}
