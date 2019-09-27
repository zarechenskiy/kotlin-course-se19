package ru.hse.spb

import ru.hse.spb.parser.FunCallBaseVisitor
import ru.hse.spb.parser.FunCallParser.*

val Boolean.int
    get() = if (this) 1 else 0

class StatementsEvaluationVisitor : FunCallBaseVisitor<Any>() {
    private var varEnv = HashMap<String, Int?>()
    private var funcEnv = HashMap<String, FuncContext>()
    private var wasRet = false
    private var retValue = 0


    override fun visitFile(ctx: FileContext) {
        visit(ctx.block())
    }

    override fun visitBlock(ctx: BlockContext) {
        wasRet = false
        retValue = 0
        for (statement in ctx.statement()) {
            visit(statement)
            if (wasRet) break
        }
    }

    override fun visitBrBlock(ctx: BrBlockContext) {
        val varEnvBefore = HashMap(varEnv)
        val funcEnvBefore = HashMap(funcEnv)

        visit(ctx.block())

        varEnv.filter { varEnvBefore.containsKey(it.key) }
        funcEnv.filter { funcEnvBefore.containsKey(it.key) }
    }

    override fun visitStatement(ctx: StatementContext): Any = visit(ctx.getChild(0))

    override fun visitProdExpr(ctx: ProdExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Int }
        return when (ctx.op.type) {
            MUL  -> left * right
            DIV  -> left / right
            MOD  -> left % right
            else -> throw RuntimeException("line ${ctx.start.line}: error `prod` operator")
        }
    }

    override fun visitSumExpr(ctx: SumExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Int }
        return when (ctx.op.type) {
            PLUS  -> left + right
            MINUS -> left - right
            else  -> throw RuntimeException("line ${ctx.start.line}: error `sum` operator")
        }
    }

    override fun visitOrdExpr(ctx: OrdExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Int }
        return when (ctx.op.type) {
            EQ   -> (left == right).int
            NEQ  -> (left != right).int
            GE   -> (left > right).int
            LE   -> (left < right).int
            GEQ  -> (left >= right).int
            LEQ  -> (left <= right).int
            else -> throw RuntimeException("line ${ctx.start.line}: error `ord` operator")
        }
    }

    override fun visitAndExpr(ctx: AndExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Boolean }
        return when (ctx.op.type) {
            AND  -> (left && right).int
            else -> throw RuntimeException("line ${ctx.start.line}: error `and` operator")
        }
    }

    override fun visitOrExpr(ctx: OrExprContext): Int {
        val (left, right) = ctx.expr().map { visit(it) as Boolean }
        return when (ctx.op.type) {
            OR   -> (left || right).int
            else -> throw RuntimeException("line ${ctx.start.line}: error `or` operator")
        }
    }

    override fun visitUnaryExpr(ctx: UnaryExprContext): Int {
        val value = visit(ctx.expr())
        return when (ctx.op.type) {
            MINUS -> -(value as Int)
            EXC   -> (!(value as Boolean)).int
            else  -> throw RuntimeException("line ${ctx.start.line}: error `unary` operator")
        }
    }

    override fun visitLitExpr(ctx: LitExprContext): Int = ctx.text.toInt()

    override fun visitIdExpr(ctx: IdExprContext): Int {
        val varName = ctx.IDENTIFIER().text
        if (varEnv.containsKey(varName)) {
            return varEnv[varName]
                ?: throw RuntimeException("line ${ctx.start.line}: using defined but not assignment variable")
        }
        throw RuntimeException("line ${ctx.start.line}: using undefined variable")
    }

    override fun visitBrExpr(ctx: BrExprContext): Int = visit(ctx.expr()) as Int

    override fun visitVar(ctx: VarContext) {
        val varName = ctx.IDENTIFIER().text
        if (varEnv.containsKey(varName)) {
            throw RuntimeException("line ${ctx.start.line}: duplicate define of variable")
        }
        varEnv[varName] = if (ctx.childCount == 3) null else (visit(ctx.expr()) as Int)
    }

    override fun visitAssign(ctx: AssignContext) {
        val varName = ctx.IDENTIFIER().text
        if (!varEnv.containsKey(varName)) {
            throw RuntimeException("line ${ctx.start.line}: assignment to undefined variable")
        }
        varEnv[varName] = visit(ctx.expr()) as Int
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
        retValue = visit(ctx.expr()) as Int
        wasRet = true
    }

    override fun visitFunc(ctx: FuncContext) {
        val funcName = ctx.IDENTIFIER().text
        if (funcEnv.containsKey(funcName)) {
            throw RuntimeException("line ${ctx.start.line}: duplicate define of function")
        }
        funcEnv[funcName] = ctx
    }

    override fun visitCallExpr(ctx: CallExprContext): Int {
        val funcName = ctx.IDENTIFIER().text
        val func = funcEnv[funcName] ?: throw RuntimeException("line ${ctx.start.line}: undefined function")

        val varEnvBefore = HashMap(varEnv)
        val funcEnvBefore = HashMap(funcEnv)

        val args = visit(ctx.args()) as List<*>
        val params = visit(func.params()) as List<*>
        params.zip(args).forEach { varEnv[it.first as String] = it.second as Int }

        visit(func.brBlock())

        varEnv = varEnvBefore
        funcEnv = funcEnvBefore

        val res = retValue

        wasRet = false
        retValue = 0

        return res
    }

    override fun visitParams(ctx: ParamsContext): List<String> = ctx.IDENTIFIER().map { it.text }

    override fun visitArgs(ctx: ArgsContext): List<Int> = ctx.expr().map { visit(it) as Int }

    override fun visitPrintExpr(ctx: PrintExprContext) {
        (visit(ctx.args()) as List<*>).forEach { println(it) }
    }
}
