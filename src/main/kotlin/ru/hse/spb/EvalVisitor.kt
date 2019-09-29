package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.RuleNode
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import java.rmi.UnexpectedException
import java.util.*
import kotlin.collections.HashMap

sealed class Expr
data class Func(val params: List<String>, val block: ExpParser.BlockContext) : Expr()
data class Var(var value: Any) : Expr() {
    fun isInt() = value is Int
    fun asInt() = value as Int

    fun isBoolean() = value is Boolean
    fun asBoolean() = value as Boolean

    fun createContext(last: Boolean = false) = ValueContext(this, last)
}

data class ValueContext(val value: Var, val last: Boolean = false) {
    companion object {
        val ZERO = ValueContext(Var(0))
    }
}

typealias Scope = MutableMap<String, Expr>

class EvalVisitor : ExpBaseVisitor<ValueContext>() {
    private class Context {
        private val stack = Stack<Scope>().apply { push(HashMap()) }

        inline fun <reified T : Expr> find(id: String) = stack.findLast { it[id] is T }?.get(id) as T?

        inline fun scope(block: Scope.() -> ValueContext): ValueContext {
            return stack.peek().block()
        }

        inline fun newScope(block: Scope.() -> ValueContext): ValueContext {
            stack.push(HashMap())
            val result = stack.peek().block()
            stack.pop()
            return result
        }
    }

    private val context = Context()

    override fun visitBlock(ctx: ExpParser.BlockContext) = context.newScope {
        var result = ValueContext.ZERO
        ctx.statement().forEach { statement -> if (!result.last) result = visitStatement(statement) }
        result
    }

    override fun visitFunc(ctx: ExpParser.FuncContext) = context.scope {
        val name = ctx.IDENTIFIER().text
        val params = ctx.params().IDENTIFIER().map { it.text }

        if (containsKey(name)) throw IllegalArgumentException("line ${ctx.start.line}: function with name '$name' already exists")
        put(name, Func(params, ctx.brBlock().block()))

        ValueContext.ZERO
    }

    override fun visitVar(ctx: ExpParser.VarContext) = context.scope {
        val name = ctx.IDENTIFIER().text
        val value = visit(ctx.expr()).value

        if (containsKey(name)) throw IllegalArgumentException("line ${ctx.start.line}: var with name '$name' already exists")
        put(name, value)

        ValueContext.ZERO
    }

    override fun visitCall(ctx: ExpParser.CallContext) = context.scope {
        val name = ctx.IDENTIFIER().text

        val result = if (isNative(name)) {
            ctx.exec(name, ctx.args().expr().map { visit(it).value }.toList())
        } else {
            val func = context.find<Func>(name) ?: throw NoSuchMethodException(name)

            if (ctx.args().expr().size != func.params.size) throw IllegalArgumentException("line ${ctx.start.line}: parameters don't much function prototype - $name")

            context.newScope {
                ctx.args().expr().zip(func.params).forEach { (expr, pname) -> put(pname, visit(expr).value) }
                visitBlock(func.block)
            }
        }

        if (result.last) result.copy(last = false) else ValueContext.ZERO
    }

    override fun visitAsgn(ctx: ExpParser.AsgnContext) = context.scope {
        val name = ctx.IDENTIFIER().text
        val variable = context.find<Var>(name) ?: throw NoSuchFieldException("line ${ctx.start.line}: $name")

        val newValue = visit(ctx.expr()).value
        variable.value = newValue.value

        ValueContext.ZERO
    }

    override fun visitRet(ctx: ExpParser.RetContext) = visit(ctx.expr()).value.createContext(last = true)

    override fun visitCond(ctx: ExpParser.CondContext) = context.scope {
        val cond = visit(ctx.expr()).value

        if (!cond.isBoolean()) throw UnsupportedOperationException("line ${ctx.start.line}: using conditional operator with non boolean type")

        when {
            cond.asBoolean() -> visitBlock(ctx.brBlock().first().block())
            ctx.brBlock().size > 1 -> visitBlock(ctx.brBlock().last().block())
            else -> ValueContext.ZERO
        }
    }

    override fun visitWhl(ctx: ExpParser.WhlContext) = context.scope {
        var cond = visit(ctx.expr()).value

        if (!cond.isBoolean()) throw UnsupportedOperationException("line ${ctx.start.line}: using 'while' operator with non boolean type")

        var result = ValueContext.ZERO
        while (cond.asBoolean()) {
            result = visitBrBlock(ctx.brBlock())

            if (result.last) break

            cond = visit(ctx.expr()).value
            if (!cond.isBoolean()) throw UnsupportedOperationException("line ${ctx.start.line}: using 'while' operator with non boolean type")
        }

        result
    }

    override fun visitUnaryExpr(ctx: ExpParser.UnaryExprContext) = context.scope {
        val result = visit(ctx.expr()).value

        if (result.isInt()) Var(-result.asInt()).createContext()
        else throw UnsupportedOperationException("line ${ctx.start.line}: unary minus with boolean type")
    }

    override fun visitMultExpr(ctx: ExpParser.MultExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        if (leftResult.isInt() && rightResult.isInt()) {
            Var(when (ctx.op.type) {
                ExpParser.MULT -> leftResult.asInt() * rightResult.asInt()
                ExpParser.DIV -> leftResult.asInt() / rightResult.asInt()
                ExpParser.MOD -> leftResult.asInt() % rightResult.asInt()
                else -> throw UnexpectedException("line ${ctx.start.line}: non multiplicative operator in the multiplicative block")
            }).createContext()
        } else throw UnsupportedOperationException("line ${ctx.start.line}: using multiplicative operator with boolean type")
    }

    override fun visitAddExpr(ctx: ExpParser.AddExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        if (leftResult.isInt() && rightResult.isInt()) {
            Var(when (ctx.op.type) {
                ExpParser.PLUS -> leftResult.asInt() + rightResult.asInt()
                ExpParser.MINUS -> leftResult.asInt() - rightResult.asInt()
                else -> throw UnexpectedException("line ${ctx.start.line}: non additive operator in the additive block")
            }).createContext()
        } else throw UnsupportedOperationException("line ${ctx.start.line}: using additive operator with boolean type")
    }

    override fun visitRelExpr(ctx: ExpParser.RelExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        if (leftResult.isInt() && rightResult.isInt()) {
            Var(when (ctx.op.type) {
                ExpParser.LTEQ -> leftResult.asInt() <= rightResult.asInt()
                ExpParser.GTEQ -> leftResult.asInt() >= rightResult.asInt()
                ExpParser.LT -> leftResult.asInt() < rightResult.asInt()
                ExpParser.GT -> leftResult.asInt() > rightResult.asInt()
                else -> throw UnexpectedException("line ${ctx.start.line}: non relational operator in the relational block")
            }).createContext()
        } else throw UnsupportedOperationException("line ${ctx.start.line}: using relational operator with boolean type")
    }

    override fun visitEqExpr(ctx: ExpParser.EqExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        Var(when (ctx.op.type) {
            ExpParser.EQ -> leftResult == rightResult
            ExpParser.NEQ -> leftResult != rightResult
            else -> throw UnexpectedException("line ${ctx.start.line}: non equality operator in the equality block")
        }).createContext()
    }

    override fun visitAndExpr(ctx: ExpParser.AndExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        if (leftResult.isBoolean() && rightResult.isBoolean()) Var(leftResult.asBoolean() && rightResult.asBoolean()).createContext()
        else throw UnsupportedOperationException("line ${ctx.start.line}: using 'and' operator with non boolean type")
    }

    override fun visitOrExpr(ctx: ExpParser.OrExprContext) = context.scope {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value

        if (leftResult.isBoolean() && rightResult.isBoolean()) Var(leftResult.asBoolean() || rightResult.asBoolean()).createContext()
        else throw UnsupportedOperationException("line ${ctx.start.line}: using 'or' operator with non boolean type")
    }

    override fun visitIdExpr(ctx: ExpParser.IdExprContext) = context.scope {
        val name = ctx.IDENTIFIER().text
        val value = context.find<Var>(name) ?: throw NoSuchFieldException("line ${ctx.start.line}: $name")
        value.createContext()
    }

    override fun visitBoolExpr(ctx: ExpParser.BoolExprContext) = Var(ctx.text!!.toBoolean()).createContext()

    override fun visitLitExpr(ctx: ExpParser.LitExprContext) = Var(ctx.text!!.toInt()).createContext()

    override fun visitParExpr(ctx: ExpParser.ParExprContext) = context.scope { visit(ctx.expr()) }

    override fun shouldVisitNextChild(node: RuleNode, currentResult: ValueContext?) = currentResult?.let { !it.last }
            ?: true

    override fun defaultResult() = ValueContext.ZERO

    private fun isNative(name: String) = name == "println"
    private fun ParserRuleContext.exec(name: String, params: List<Var>): ValueContext {
        when (name) {
            "println" -> {
                println(params.map { it.value }.joinToString(separator = " "))
            }
            else -> throw NoSuchMethodException("line ${start.line}: $name")
        }

        return ValueContext.ZERO
    }
}
