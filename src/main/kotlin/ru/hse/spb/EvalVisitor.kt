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

class EvalVisitor : ExpBaseVisitor<ValueContext>() {
    private val stack = Stack<MutableMap<String, Expr>>()

    private inline fun <reified T : Expr> find(id: String) = stack.findLast {
        it[id]?.let { expr -> expr is T } ?: false
    }?.get(id) as T?

    override fun visitBlock(ctx: ExpParser.BlockContext): ValueContext {
        stack.push(HashMap())

        var result = ValueContext.ZERO
        ctx.statement().forEach { statement -> if (!result.last) result = visitStatement(statement) }

        stack.pop()
        return result
    }

    override fun visitFunc(ctx: ExpParser.FuncContext): ValueContext {
        val name = ctx.IDENTIFIER().text
        val params = ctx.params().IDENTIFIER().map { it.text }

        with(stack.peek()) {
            if (containsKey(name)) throw IllegalArgumentException("line ${ctx.start.line}: function with name '$name' already exists")
            put(name, Func(params, ctx.brBlock().block()))
        }

        return ValueContext.ZERO
    }

    override fun visitVar(ctx: ExpParser.VarContext): ValueContext {
        val name = ctx.IDENTIFIER().text
        val value = visit(ctx.expr()).value

        with(stack.peek()) {
            if (containsKey(name)) throw IllegalArgumentException("line ${ctx.start.line}: var with name '$name' already exists")
            put(name, value)
        }

        return ValueContext.ZERO
    }

    override fun visitCall(ctx: ExpParser.CallContext): ValueContext {
        val name = ctx.IDENTIFIER().text
        val line = ctx.IDENTIFIER().symbol.line

        val result: ValueContext
        if (isNative(name)) {
            result = ctx.exec(name, ctx.args().expr().map { visit(it).value }.toList())
        } else {
            val func = find<Func>(name) ?: throw NoSuchMethodException(name)

            if (ctx.args().expr().size != func.params.size) throw IllegalArgumentException("line $line: parameters don't much function prototype - $name")
            stack.push(HashMap<String, Expr>().apply {
                ctx.args().expr().zip(func.params).forEach { (expr, pname) -> put(pname, visit(expr).value) }
            })

            result = visitBlock(func.block)
            stack.pop()
        }

        return if (result.last) result.copy(last = false) else ValueContext.ZERO
    }

    override fun visitAsgn(ctx: ExpParser.AsgnContext): ValueContext {
        val name = ctx.IDENTIFIER().text
        val variable = find<Var>(name) ?: throw NoSuchFieldException("line ${ctx.start.line}: $name")

        val newValue = visit(ctx.expr()).value
        variable.value = newValue.value

        return ValueContext.ZERO
    }

    override fun visitRet(ctx: ExpParser.RetContext): ValueContext {
        return visit(ctx.expr()).value.createContext(last = true)
    }

    override fun visitCond(ctx: ExpParser.CondContext): ValueContext {
        val cond = visit(ctx.expr()).value
        val line = ctx.start.line

        if (!cond.isBoolean()) throw UnsupportedOperationException("line $line: using conditional operator with non boolean type")

        var result = ValueContext.ZERO
        if (cond.asBoolean()) {
            result = visitBlock(ctx.brBlock().first().block())
        } else if (ctx.brBlock().size > 1) {
            result = visitBlock(ctx.brBlock().last().block())
        }

        return result
    }

    override fun visitWhl(ctx: ExpParser.WhlContext): ValueContext {
        var cond = visit(ctx.expr()).value
        val line = ctx.start.line

        if (!cond.isBoolean()) throw UnsupportedOperationException("line $line: using 'while' operator with non boolean type")

        while (cond.asBoolean()) {
            val result = visitBrBlock(ctx.brBlock())

            if (result.last) return result

            cond = visit(ctx.expr()).value
            if (!cond.isBoolean()) throw UnsupportedOperationException("line $line: using 'while' operator with non boolean type")
        }

        return ValueContext.ZERO
    }

    override fun visitUnaryExpr(ctx: ExpParser.UnaryExprContext): ValueContext {
        val result = visit(ctx.expr()).value
        val line = ctx.start.line

        if (result.isInt()) return Var(-result.asInt()).createContext()
        else throw UnsupportedOperationException("line $line: unary minus with boolean type")
    }

    override fun visitMultExpr(ctx: ExpParser.MultExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        if (leftResult.isInt() && rightResult.isInt()) {
            return Var(when (ctx.op.type) {
                ExpParser.MULT -> leftResult.asInt() * rightResult.asInt()
                ExpParser.DIV -> leftResult.asInt() / rightResult.asInt()
                ExpParser.MOD -> leftResult.asInt() % rightResult.asInt()
                else -> throw UnexpectedException("line $line: non multiplicative operator in the multiplicative block")
            }).createContext()
        } else throw UnsupportedOperationException("line $line: using multiplicative operator with boolean type")
    }

    override fun visitAddExpr(ctx: ExpParser.AddExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        if (leftResult.isInt() && rightResult.isInt()) {
            return Var(when (ctx.op.type) {
                ExpParser.PLUS -> leftResult.asInt() + rightResult.asInt()
                ExpParser.MINUS -> leftResult.asInt() - rightResult.asInt()
                else -> throw UnexpectedException("line $line: non additive operator in the additive block")
            }).createContext()
        } else throw UnsupportedOperationException("line $line: using additive operator with boolean type")
    }

    override fun visitRelExpr(ctx: ExpParser.RelExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        if (leftResult.isInt() && rightResult.isInt()) {
            return Var(when (ctx.op.type) {
                ExpParser.LTEQ -> leftResult.asInt() <= rightResult.asInt()
                ExpParser.GTEQ -> leftResult.asInt() >= rightResult.asInt()
                ExpParser.LT -> leftResult.asInt() < rightResult.asInt()
                ExpParser.GT -> leftResult.asInt() > rightResult.asInt()
                else -> throw UnexpectedException("line $line: non relational operator in the relational block")
            }).createContext()
        } else throw UnsupportedOperationException("line $line: using relational operator with boolean type")
    }

    override fun visitEqExpr(ctx: ExpParser.EqExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        return Var(when (ctx.op.type) {
            ExpParser.EQ -> leftResult == rightResult
            ExpParser.NEQ -> leftResult != rightResult
            else -> throw UnexpectedException("line $line: non equality operator in the equality block")
        }).createContext()
    }

    override fun visitAndExpr(ctx: ExpParser.AndExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        if (leftResult.isBoolean() && rightResult.isBoolean()) {
            return Var(leftResult.asBoolean() && rightResult.asBoolean()).createContext()
        } else throw UnsupportedOperationException("line $line: using 'and' operator with non boolean type")
    }

    override fun visitOrExpr(ctx: ExpParser.OrExprContext): ValueContext {
        val (left, right) = ctx.expr()
        val leftResult = visit(left).value
        val rightResult = visit(right).value
        val line = ctx.start.line

        if (leftResult.isBoolean() && rightResult.isBoolean()) {
            return Var(leftResult.asBoolean() || rightResult.asBoolean()).createContext()
        } else throw UnsupportedOperationException("line $line: using 'or' operator with non boolean type")
    }

    override fun visitIdExpr(ctx: ExpParser.IdExprContext): ValueContext {
        val name = ctx.IDENTIFIER().text
        val value = find<Var>(name) ?: throw NoSuchFieldException("line ${ctx.start.line}: $name")

        return value.createContext()
    }

    override fun visitBoolExpr(ctx: ExpParser.BoolExprContext) = Var(ctx.text!!.toBoolean()).createContext()

    override fun visitLitExpr(ctx: ExpParser.LitExprContext) = Var(ctx.text!!.toInt()).createContext()

    override fun visitParExpr(ctx: ExpParser.ParExprContext): ValueContext = visit(ctx.expr())

    override fun shouldVisitNextChild(node: RuleNode, currentResult: ValueContext?) = currentResult?.let { !it.last } ?: true
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
