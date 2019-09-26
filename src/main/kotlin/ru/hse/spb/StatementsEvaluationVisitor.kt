package ru.hse.spb

import ru.hse.spb.parser.FunCallBaseVisitor
import ru.hse.spb.parser.FunCallParser.*
import java.rmi.UnexpectedException

val Boolean.int
    get() = if (this) 1 else 0

val Int.bool
    get() = this != 0

class StatementsEvaluationVisitor : FunCallBaseVisitor<Any>() {
    private var varEnv = HashMap<String, Int?>()
    private var funcEnv = HashMap<String, FuncContext>()
    private var wasRet = false
    private var retValue = 0

    override fun visitFile(ctx: FileContext) {
        visit(ctx.block())
    }

    override fun visitBlock(ctx: BlockContext) {
        val varEnvBefore = HashMap(varEnv)
        val funcEnvBefore = HashMap(funcEnv)
        for (statement in ctx.statement()) {
            visit(statement)
            if (wasRet) break
        }

        varEnv.filter { varEnvBefore.containsKey(it.key) }
        funcEnv.filter { funcEnvBefore.containsKey(it.key) }
    }

    override fun visitBrBlock(ctx: BrBlockContext): Any = visit(ctx.block())

    override fun visitStatement(ctx: StatementContext): Any = visit(ctx.getChild(0))

    override fun visitProdExpr(ctx: ProdExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Int }
        return when (ctx.op.type) {
            MUL -> left * right
            DIV -> left / right
            MOD -> left % right
            else -> throw UnexpectedException("line ${ctx.start.line}: error `prod` operator")
        }
    }

    override fun visitSumExpr(ctx: SumExprContext): Int {
        val (left, right) = ctx.expr().map { it -> visit(it) as Int }
        return when (ctx.op.type) {
            PLUS -> left + right
            MINUS -> left - right
            else -> throw UnexpectedException("line ${ctx.start.line}: error `sum` operator")
        }
    }

    override fun visitOrdExpr(ctx: OrdExprContext): Int {
        val (left, right) = ctx.expr().map { it -> visit(it) as Int }
        return when (ctx.op.type) {
            EQ -> (left == right).int
            NEQ -> (left != right).int
            GE -> (left > right).int
            LE -> (left < right).int
            GEQ -> (left >= right).int
            LEQ -> (left <= right).int
            else -> throw UnexpectedException("line ${ctx.start.line}: error `ord` operator")
        }
    }

    override fun visitAndExpr(ctx: AndExprContext): Boolean {
        val (left, right) = ctx.expr().map { it -> visit(it) as Boolean }
        return when (ctx.op.type) {
            AND  -> left && right
            else -> throw UnexpectedException("line ${ctx.start.line}: error `and` operator")
        }
    }

    override fun visitOrExpr(ctx: OrExprContext): Boolean {
        val (left, right) = ctx.expr().map { it -> visit(it) as Boolean }
        return when (ctx.op.type) {
            OR   -> left || right
            else -> throw UnexpectedException("line ${ctx.start.line}: error `or` operator")
        }
    }

    override fun visitUnaryExpr(ctx: UnaryExprContext): Any {
        val value = visit(ctx.expr()) as Int
        return when (ctx.op.type) {
            MINUS -> -value
            EXC   -> !value.bool
            else  -> throw UnexpectedException("line ${ctx.start.line}: error `unary` operator")
        }
    }

    override fun visitLitExpr(ctx: LitExprContext): Int = ctx.text.toInt()

    override fun visitIdExpr(ctx: IdExprContext): Int {
        val varName = ctx.IDENTIFIER().text
        if (varEnv.containsKey(varName)) {
            return varEnv[varName]
                ?: throw UnexpectedException("line ${ctx.start.line}: using defined but not assignment variable")
        } else {
            throw UnexpectedException("line ${ctx.start.line}: using undefined variable")
        }
    }

    override fun visitBrExpr(ctx: BrExprContext): Int = visit(ctx.expr()) as Int

    override fun visitVar(ctx: VarContext) {
        val varName = ctx.IDENTIFIER().text
        if (varEnv.containsKey(varName)) {
            throw UnexpectedException("line ${ctx.start.line}: duplicate define of variable")
        }
        varEnv[varName] = if (ctx.childCount == 3) null else (visit(ctx.expr()) as Int)
    }

    override fun visitAssign(ctx: AssignContext) {
        val varName = ctx.IDENTIFIER().text
        if (varEnv.containsKey(varName)) {
            val varValue = visit(ctx.expr()) as Int
            varEnv[varName] = varValue
        } else {
            throw UnexpectedException("line ${ctx.start.line}: assignment to undefined variable")
        }
    }

    override fun visitIfSt(ctx: IfStContext) {
        if (visit(ctx.expr()) != 0) {
            visit(ctx.brBlock(0))
        } else if (ctx.brBlock().size == 2) {
            visit(ctx.brBlock(1))
        }
    }

    override fun visitWhileSt(ctx: WhileStContext) {
        while (visit(ctx.expr()) != 0) {
            visit(ctx.brBlock())
        }
    }

    override fun visitReturnSt(ctx: ReturnStContext) {
        wasRet = true
        retValue = visit(ctx.expr()) as Int
    }

    override fun visitFunc(ctx: FuncContext) {
        val funcName = ctx.IDENTIFIER().text
        if (funcEnv.containsKey(funcName)) {
            throw UnexpectedException("line ${ctx.start.line}: duplicate define of function")
        }
        funcEnv[funcName] = ctx
    }

    override fun visitCallExpr(ctx: CallExprContext): Int {
        val funcName = ctx.IDENTIFIER().text
        val func = funcEnv[funcName] ?: throw UnexpectedException("line ${ctx.start.line}: undefined function")

        val args = visit(ctx.args()) as List<Int>
        val params = visit(func.params()) as List<String>
        params.zip(args).forEach { varEnv[it.first] = it.second }

        visit(func.brBlock())
        if (wasRet) {
            wasRet = false
            return retValue
        }
        return 0
    }

    override fun visitParams(ctx: ParamsContext): List<String> {
        return ctx.IDENTIFIER().map { it.text }
    }

    override fun visitArgs(ctx: ArgsContext): List<Int> {
        return ctx.expr().map {
            val arg = visit(it)
            if (arg !is Int) {
                throw UnexpectedException("line ${ctx.start.line}: error function call argument")
            } else {
                arg
            }
        }
    }

    override fun visitPrintExpr(ctx: PrintExprContext) {
        (visit(ctx.args()) as List<*>).forEach { println(it) }
    }
}