package ru.hse.spb

import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser.*
import java.io.PrintStream
import java.lang.RuntimeException

class FunVisitor(private val outputStream : PrintStream = System.out) : FunBaseVisitor<Any?>() {
    private val blockEnv = BlockEnv()
    var returnRegister: Any? = null

    override fun visitFile(ctx: FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: BlockContext) {
        val oldVarsMap = blockEnv.deepCopyVarMap()
        val oldFunctionsMap = blockEnv.deepCopyFunctionsMap()

        for (statement in ctx.statement()) {
            if (returnRegister != null) {
                break
            }

            statement.accept(this)
        }

        val toDeleteVars = emptyList<String>().toMutableList()
        val toDeleteFunctions = emptyList<String>().toMutableList()

        blockEnv.deepCopyVarMap().keys.forEach { if (it !in oldVarsMap) toDeleteVars += it }
        blockEnv.deepCopyFunctionsMap().keys.forEach { if (it !in oldFunctionsMap) toDeleteFunctions += it }

        toDeleteVars.forEach { blockEnv.deleteVar(it) }
        toDeleteFunctions.forEach { blockEnv.deleteFunction(it) }
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext) {
        ctx.block().accept(this)
    }

    override fun visitStatement(ctx: StatementContext) {
        ctx.getChild(0).accept(this)
    }

    override fun visitIfRule(ctx: IfRuleContext) {
        if (ctx.expression().accept(this) as Boolean) {
            ctx.blockWithBraces(0).accept(this)
        } else if (ctx.blockWithBraces().size == 2) {
            ctx.blockWithBraces(1).accept(this)
        }
    }

    override fun visitWhileRule(ctx: WhileRuleContext) {
        while (ctx.expression().accept(this) as Boolean) {
            ctx.blockWithBraces().accept(this)
        }
    }

    override fun visitReturnRule(ctx: ReturnRuleContext) {
        returnRegister = ctx.expression().accept(this)
    }

    override fun visitAssignment(ctx: AssignmentContext) {
        blockEnv.assignVar(ctx.IDENTIFIER().toString(), ctx.expression().accept(this))
    }

    override fun visitFunction(ctx: FunctionContext) {
        val fName = ctx.IDENTIFIER(0).toString()
        blockEnv.addFunction(fName, ctx)
    }

    override fun visitVariable(ctx: VariableContext) {
        val vName = ctx.IDENTIFIER().symbol.text
        blockEnv.assignVar(vName, if (ctx.childCount == 1) null else ctx.expression().accept(this))
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): Any? {
        val fName = ctx.IDENTIFIER().text
        val args = ctx.arguments.map { it.accept(this) }

        if (fName == "println") {
            args.forEach {
                outputStream.print("$it ")
            }
            outputStream.println()

            return Unit
        }

        val fCtx = blockEnv.getFunction(fName)
        val paramNames = fCtx.parameters.map { it.text }

        if (paramNames.size != args.size) {
            throw RuntimeException("Wrong number of arguments for function $fName")
        }

        val oldVarMap = blockEnv.deepCopyVarMap()
        paramNames.zip(args).forEach { blockEnv.assignVar(it.first, it.second) }
        fCtx.blockWithBraces().accept(this)

        paramNames.forEach {
            if (it in oldVarMap) {
                blockEnv.assignVar(it, oldVarMap[it])
            } else {
                blockEnv.deleteVar(it)
            }
        }

        val ret = returnRegister
        returnRegister = null
        return ret ?: 0
    }

    override fun visitUnaryMinus(ctx: UnaryMinusContext): Any {
        return -(ctx.expression().accept(this) as Int)
    }

    override fun visitCompExpr(ctx: CompExprContext): Any {
        val left = ctx.expression(0).accept(this) as Int
        val right = ctx.expression(1).accept(this) as Int

        return when (ctx.operator.type) {
            FunLexer.LT -> left < right
            FunLexer.LTEQ -> left <= right
            FunLexer.GT -> left > right
            FunLexer.GTEQ -> left >= right
            else -> throw RuntimeException("Unknown operator for comparison.")
        }
    }

    override fun visitMulExpr(ctx: MulExprContext): Any {
        val left = ctx.expression(0).accept(this) as Int
        val right = ctx.expression(1).accept(this) as Int

        return when (ctx.operator.type) {
            FunLexer.MULT -> left * right
            FunLexer.DIV -> left / right
            FunLexer.MOD -> left % right
            else -> error("Unknown operator for multiplication.")
        }
    }

    override fun visitAddExpr(ctx: AddExprContext): Any {
        val left = ctx.expression(0).accept(this) as Int
        val right = ctx.expression(1).accept(this) as Int

        return when (ctx.operator.type) {
            FunLexer.MINUS -> left - right
            FunLexer.PLUS -> left + right
            else -> error("Unknown operator for addition.")
        }
    }

    override fun visitEqExpr(ctx: EqExprContext): Any {
        val left = ctx.expression(0).accept(this)
        val right = ctx.expression(1).accept(this)

        return when (ctx.operator.type) {
            FunLexer.EQ -> left == right
            FunLexer.NEQ -> left != right
            else -> error("Unknown operator for equivalence.")
        }
    }

    override fun visitAndExpr(ctx: AndExprContext): Any {
        val left = ctx.expression(0).accept(this) as Boolean
        val right = ctx.expression(1).accept(this) as Boolean

        return left && right
    }

    override fun visitOrExpr(ctx: OrExprContext): Any {
        val left = ctx.expression(0).accept(this) as Boolean
        val right = ctx.expression(1).accept(this) as Boolean

        return left || right
    }

    override fun visitIdentExpr(ctx: IdentExprContext): Any {
        val varName = ctx.IDENTIFIER().toString()
        return blockEnv.getVar(varName)
    }

    override fun visitNumExpr(ctx: NumExprContext): Any {
        return ctx.NUMBER_LITERAL().toString().toInt()
    }
}