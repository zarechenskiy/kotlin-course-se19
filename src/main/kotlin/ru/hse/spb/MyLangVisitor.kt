package ru.hse.spb

import ru.hse.spb.parser.MyLangBaseVisitor
import ru.hse.spb.parser.MyLangParser

class MyLangVisitor : MyLangBaseVisitor<Context>() {
    val scope = Scope()

    //Definitions
    override fun visitLVar(ctx: MyLangParser.LVarContext): Context {
        val name = ctx.IDENTIFIER().text
        scope.declareVariable(name)

        ctx.lExpr()?.let {
            scope.setVariable(name, visit(it))
        }

        return Context.NONE
    }

    override fun visitLFun(ctx: MyLangParser.LFunContext): Context {
        scope.withFunction(ctx.IDENTIFIER().text, ctx)
        return Context.NONE
    }

    override fun visitLAssignment(ctx: MyLangParser.LAssignmentContext): Context = withContext(ctx) {
        val name = ctx.IDENTIFIER().text
        val value = visit(ctx.lExpr())

        scope.setVariable(name, value)

        Context.NONE
    }

    override fun visitLFuncCall(ctx: MyLangParser.LFuncCallContext): Context = withContext(ctx) {
        val name = ctx.IDENTIFIER().text

        val argValues = ctx.lArgs().lExpr().map { visit(it) }

        if (MixinFunctions.isMixin(name)) return MixinFunctions.execute(name, argValues)

        val func = scope.findFunction(name)!!
        val argNames = func.lParams().IDENTIFIER().map { it.text }

        require(argNames.size == argValues.size) { "Unexpected number of parameters in function $name" }

        return scope.withFrame {
            argNames.zip(argValues).forEach { (name, value) ->
                scope.declareVariable(name)
                scope.setVariable(name, value)
            }
            var result: Context? = null
            try {
                visit(func.lBlockWithBraces())
            } catch (e: ReturnException) {
                result = e.context
            }
            result ?: Context.ZERO
        }
    }

    override fun visitLWhile(ctx: MyLangParser.LWhileContext): Context = withContext(ctx) {
        while (visit(ctx.lCond).toBool()) {
            visit(ctx.lBody)
        }
        return Context.NONE
    }

    override fun visitLIf(ctx: MyLangParser.LIfContext): Context = withContext(ctx) {
        val cond = visit(ctx.lCond)
        return if (cond.toBool()) {
            visit(ctx.lThen)
        } else {
            ctx.lElse?.let { visit(it) } ?: Context.NONE
        }
    }

    //Blocks
    override fun visitLBlockWithBraces(ctx: MyLangParser.LBlockWithBracesContext): Context = withContext(ctx) { scope.withFrame { super.visitLBlock(ctx.lBlock()) } }

    override fun visitLBlock(ctx: MyLangParser.LBlockContext): Context = withContext(ctx) {
        for (statement in ctx.lStatement()) {
            visit(statement)
        }
        return Context.ZERO
    }

    class ReturnException(val context: Context) : Exception()
    override fun visitLReturn(ctx: MyLangParser.LReturnContext) = withContext(ctx) { throw ReturnException(visit(ctx.lExpr())) }

    //Identifier
    override fun visitLExprIdentifier(ctx: MyLangParser.LExprIdentifierContext) = withContext(ctx) { scope.findVariable(ctx.text) ?: Context.NONE }

    //Binary operations
    override fun visitLExprBinaryAdd(ctx: MyLangParser.LExprBinaryAddContext) = withContext(ctx) { Context.ofInt(visit(ctx.left).toInt() + visit(ctx.right).toInt()) }

    override fun visitLExprBinarySubtract(ctx: MyLangParser.LExprBinarySubtractContext) = withContext(ctx) { Context.ofInt(visit(ctx.left).toInt() - visit(ctx.right).toInt()) }

    override fun visitLExprBinaryDiv(ctx: MyLangParser.LExprBinaryDivContext) = withContext(ctx) { Context.ofInt(visit(ctx.left).toInt() / visit(ctx.right).toInt()) }
    override fun visitLExprBinaryMult(ctx: MyLangParser.LExprBinaryMultContext) = withContext(ctx) { Context.ofInt(visit(ctx.left).toInt() * visit(ctx.right).toInt()) }
    override fun visitLExprBinaryMod(ctx: MyLangParser.LExprBinaryModContext) = withContext(ctx) { Context.ofInt(visit(ctx.left).toInt() % visit(ctx.right).toInt()) }

    override fun visitLExprBinaryEq(ctx: MyLangParser.LExprBinaryEqContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() == visit(ctx.right).toInt()) }
    override fun visitLExprBinaryNeq(ctx: MyLangParser.LExprBinaryNeqContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() != visit(ctx.right).toInt()) }
    override fun visitLExprBinaryLess(ctx: MyLangParser.LExprBinaryLessContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() < visit(ctx.right).toInt()) }
    override fun visitLExprBinaryLessEq(ctx: MyLangParser.LExprBinaryLessEqContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() <= visit(ctx.right).toInt()) }
    override fun visitLExprBinaryMore(ctx: MyLangParser.LExprBinaryMoreContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() > visit(ctx.right).toInt()) }
    override fun visitLExprBinaryMoreEq(ctx: MyLangParser.LExprBinaryMoreEqContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toInt() >= visit(ctx.right).toInt()) }

    override fun visitLExprBinaryAnd(ctx: MyLangParser.LExprBinaryAndContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toBool() && visit(ctx.right).toBool()) }
    override fun visitLExprBinaryOr(ctx: MyLangParser.LExprBinaryOrContext) = withContext(ctx) { Context.ofBool(visit(ctx.left).toBool() || visit(ctx.right).toBool()) }


    //Parenthesis
    override fun visitLExprPar(ctx: MyLangParser.LExprParContext): Context = withContext(ctx) { visit(ctx.lExpr()) }

    //Unary
    override fun visitLExprUnaryMinus(ctx: MyLangParser.LExprUnaryMinusContext) = withContext(ctx) {
        val inner = visit(ctx.lExpr())
        Context.ofInt(-inner.toInt())
    }

    override fun visitLExprUnaryPlus(ctx: MyLangParser.LExprUnaryPlusContext) = withContext(ctx) {
        val inner = visit(ctx.lExpr())
        Context.ofInt(inner.toInt())
    }

    //Constants
    override fun visitLExprConstLiteral(ctx: MyLangParser.LExprConstLiteralContext) = withContext(ctx) { Context.ofInt(ctx.text) }

    override fun visitLExprConstLogical(ctx: MyLangParser.LExprConstLogicalContext) = withContext(ctx) { Context.ofBool(ctx.text) }
}
