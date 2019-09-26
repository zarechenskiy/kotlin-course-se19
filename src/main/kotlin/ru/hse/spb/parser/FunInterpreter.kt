package ru.hse.spb.parser

import org.antlr.v4.runtime.ParserRuleContext
import java.lang.IllegalStateException
import java.lang.RuntimeException

typealias Func = (ParserRuleContext, List<Int>) -> Int

class FunInterpreter: FunLangBaseVisitor<FunInterpreter.Result>() {
    private var scope = Scope()

    override fun visitFile(ctx: FunLangParser.FileContext): Result? {
        return visit(ctx.block())
    }

    override fun visitBlock(ctx: FunLangParser.BlockContext): Result? {
        scope = Scope(scope)
        for (statement in ctx.statement()) {
            visit(statement)?.let { if (it.returning)
                scope = scope.parentScope!!
                return it
            }
        }
        scope = scope.parentScope!!
        return null
    }

    override fun visitBlockWithBraces(ctx: FunLangParser.BlockWithBracesContext): Result? {
        return visit(ctx.block())
    }

    override fun visitStatement(ctx: FunLangParser.StatementContext): Result? {
        return visit(ctx.children.first())
    }

    private fun makeFunc(argNames: List<String>, ctx: FunLangParser.FunctionContext): Func {
        return {callCtx, args ->
            if (argNames.size != args.size)
                throw IllegalStateException("invalid function call")
            for (i in 0 until args.size)
                scope.addVar(argNames[i], args[i], callCtx)
            visit(ctx.blockWithBraces())?.value ?: 0
        }
    }

    override fun visitFunction(ctx: FunLangParser.FunctionContext): Result? {
        val id = ctx.IDENTIFIER().text
        val argNames = ctx.parameterNames().parameters?.map { it.text } ?: emptyList()
        scope.addFunc(id, makeFunc(argNames, ctx) , ctx)
        return null
    }

    override fun visitVariable(ctx: FunLangParser.VariableContext): Result? {
        scope.addVar(ctx.IDENTIFIER().text,
                if (ctx.expr() != null) visit(ctx.expr()).value else 0, ctx)
        return null
    }

    override fun visitExpr(ctx: FunLangParser.ExprContext): Result {
        ctx.IDENTIFIER()?.let {
            return Result(scope.getVar(it.text, ctx))
        }
        ctx.LITERAL()?.let {
            return Result(it.text.toInt())
        }
        ctx.functionCall()?.let {
            return visitFunctionCall(it)
        }
        if (ctx.expr().size == 1)
            return visitExpr(ctx.expr(0))
        val first = visitExpr(ctx.expr(0)).value
        val second = visitExpr(ctx.expr(1)).value
        return Result(ctx.MUL()?.let { first * second } ?:
                ctx.DIV()?.let { first / second } ?:
                ctx.MOD()?.let { first % second } ?:
                ctx.ADD()?.let { first + second } ?:
                ctx.SUB()?.let { first - second } ?:
                ctx.GT()?.let { (first > second).toInt() } ?:
                ctx.LS()?.let { (first < second).toInt() } ?:
                ctx.GE()?.let { (first >= second).toInt() } ?:
                ctx.LE()?.let { (first <= second).toInt() } ?:
                ctx.EQ()?.let { (first == second).toInt() } ?:
                ctx.NE()?.let { (first != second).toInt() } ?:
                ctx.AND()?.let { (first.toBoolean() && second.toBoolean()).toInt() } ?:
                ctx.OR()?.let { (first.toBoolean() || second.toBoolean()).toInt() } ?:
                throw RuntimeException("Unknown operation"))
    }

    override fun visitWhileSt(ctx: FunLangParser.WhileStContext): Result? {
        while (visitExpr(ctx.expr()).value.toBoolean()) {
            visit(ctx.blockWithBraces())?.let { if (it.returning) return it }
        }
        return null
    }

    override fun visitIfSt(ctx: FunLangParser.IfStContext): Result? {
        if (visitExpr(ctx.expr()).value.toBoolean()) {
            visit(ctx.blockWithBraces(0))?.let { if (it.returning) return it }
        } else {
            ctx.blockWithBraces(1)?.accept(this)?.let { if (it.returning) return it }
        }
        return null
    }

    override fun visitAssign(ctx: FunLangParser.AssignContext): Result? {
        scope.updateVar(ctx.IDENTIFIER().text, visitExpr(ctx.expr()).value, ctx)
        return null
    }

    override fun visitReturnSt(ctx: FunLangParser.ReturnStContext): Result {
        val result = visitExpr(ctx.expr())
        result.returning = true
        return result
    }

    override fun visitFunctionCall(ctx: FunLangParser.FunctionCallContext): Result {
        scope = Scope(scope)
        val result =  Result(scope.getFunc(ctx.IDENTIFIER().text, ctx)
                .invoke(ctx, ctx.args().expr().map { visitExpr(it).value }))
        scope = scope.parentScope!!
        return result
    }


    inner class Scope(internal val parentScope: Scope? = null) {
        private val vars = mutableMapOf<String, Int?>()
        private val funcs = mutableMapOf<String, Func>()

        init {
            if (parentScope == null) {
                funcs["println"] = { _, args ->
                    println(args.joinToString(", ") { it.toString() })
                    0}
            }
        }

        fun addVar(id: String, value: Int? = null, ctx: ParserRuleContext) {
            if (vars.containsKey(id))
                throw IllegalStateException("At line ${ctx.start.line}: variable $id is already defined")
            vars[id] = value
        }

        fun updateVar(id: String, value: Int, ctx: ParserRuleContext) {
            if (!vars.containsKey(id))
                parentScope?.updateVar(id, value, ctx) ?: throw IllegalStateException("At line ${ctx.start.line}: unknown variable $id")
            vars[id] = value
        }

        fun getVar(id: String, ctx: ParserRuleContext): Int {
            if (!vars.containsKey(id))
                return parentScope?.getVar(id, ctx) ?: throw IllegalStateException("At line ${ctx.start.line}: unknown variable $id")
            return vars[id] ?: throw IllegalStateException("At line ${ctx.start.line}: variable $id is not initialized yet")
        }

        fun addFunc(id: String, value: Func, ctx: ParserRuleContext) {
            if (funcs.containsKey(id))
                throw IllegalStateException("At line ${ctx.start.line}: function $id is already defined")
            funcs[id] = value
        }

        fun getFunc(id: String, ctx: ParserRuleContext): Func {
            return funcs[id] ?: parentScope?.getFunc(id, ctx)
                ?: throw IllegalStateException("At line ${ctx.start.line}: unknown function $id")
        }
    }

    class Result(val value: Int, var returning: Boolean = false)

    private fun Boolean.toInt() = if (this) 1 else 0
    private fun Int.toBoolean() = this != 0
}