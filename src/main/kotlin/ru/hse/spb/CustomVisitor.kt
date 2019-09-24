package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class CustomVisitor<T>() : LangBaseVisitor<T>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitBlock_with_braces(ctx: LangParser.Block_with_bracesContext?): T {
        return BlockVisitor<T>(context.copy()).visitBlock(ctx?.block())
    }

    override fun visitFunction(ctx: LangParser.FunctionContext?): T {
        return super.visitFunction(ctx)
    }

    override fun visitVariable(ctx: LangParser.VariableContext?): T {
        return super.visitVariable(ctx)
    }

    override fun visitParameter_names(ctx: LangParser.Parameter_namesContext?): T {
        return super.visitParameter_names(ctx)
    }

    override fun visitWhile_expr(ctx: LangParser.While_exprContext?): T {
        return super.visitWhile_expr(ctx)
    }

    override fun visitIf_expr(ctx: LangParser.If_exprContext?): T {
        return super.visitIf_expr(ctx)
    }

    override fun visitAssignment(ctx: LangParser.AssignmentContext?): T {
        return super.visitAssignment(ctx)
    }

    override fun visitReturn_expr(ctx: LangParser.Return_exprContext?): T {
        return super.visitReturn_expr(ctx)
    }

    override fun visitExpression(ctx: LangParser.ExpressionContext?): T {
        return super.visitExpression(ctx)
    }

    override fun visitFunction_call(ctx: LangParser.Function_callContext?): T {
        return super.visitFunction_call(ctx)
    }

    override fun visitArguments(ctx: LangParser.ArgumentsContext?): T {
        return super.visitArguments(ctx)
    }
}
