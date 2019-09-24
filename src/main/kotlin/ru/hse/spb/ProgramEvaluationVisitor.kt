package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunParser.*

class ProgramEvaluationVisitor(private var scope: Scope) : FunBaseVisitor<Int?>() {
    private var returning: Boolean = false
    private var returnValue: Int? = null

    override fun visitFile(ctx: FileContext): Int? {
        return visit(ctx.block())
    }

    override fun visitBlock(ctx: BlockContext): Int? {
        for (statement in ctx.statement()) {
            if (returning) {
                return null
            }
            visit(statement)
        }
        return null
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext): Int? {
        val oldScope = scope
        scope = Scope(scope)
        visit(ctx.block())
        scope = oldScope
        return null
    }

    override fun visitBinaryExpression(ctx: BinaryExpressionContext): Int? {
        return visit(ctx.orExpression())
    }

    override fun visitExpression(ctx: ExpressionContext): Int? {
        return visit(ctx.binaryExpression())
    }

    override fun visitStatement(ctx: StatementContext): Int? {
        return visit(ctx.children.first())
    }

    override fun visitVariable(ctx: VariableContext): Int? {
        var value: Int? = null
        if (ctx.expression() != null) {
            value = evaluate(ctx.expression())
        }
        scope.addVariable(ctx.identifier().text, value, ctx)
        return null
    }

    override fun visitAssignment(ctx: AssignmentContext): Int? {
        scope.updateVariable(ctx.identifier().text, evaluate(ctx.expression()), ctx)
        return null
    }

    override fun visitFunction(ctx: FunctionContext): Int? {
        val name = ctx.identifier().first().InternalIdentifier().text
        val argumentNames = ctx.identifier()
                .subList(1, ctx.identifier().size)
                .map { it.InternalIdentifier().text }
        scope.addFunction(name, { args ->
            if (argumentNames.size != args.size) {
                throw RuntimeException("Line ${ctx.start.line}: invalid parameters number.")
            }

            val oldScope = scope
            scope = Scope(scope)

            for (i in 0..argumentNames.lastIndex) {
                scope.addVariable(argumentNames[i], args[i], ctx)
            }
            visit(ctx.blockWithBraces())
            scope = oldScope

            returning = false
            returnValue ?: 0
        }, ctx)
        return null
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): Int? {
        val function = scope.getFunction(ctx.identifier().text, ctx)
        val arguments = ctx.expression().map { evaluate(it) }
        return function(arguments)
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): Int? {
        val value = evaluate(ctx.expression())
        returnValue = value
        returning = true
        return null
    }

    override fun visitIfCondition(ctx: IfConditionContext): Int? {
        if (toBoolean(evaluate(ctx.expression()))) {
            visit(ctx.blockWithBraces().first())
        } else if (ctx.blockWithBraces().size > 1) {
            visit(ctx.blockWithBraces().last())
        }
        return null
    }

    override fun visitWhileLoop(ctx: WhileLoopContext): Int? {
        while (toBoolean(evaluate(ctx.expression()))) {
            visit(ctx.blockWithBraces())
            if (returning) {
                return null
            }
        }
        return null
    }

    override fun visitIdentifier(ctx: IdentifierContext): Int? {
        return scope.getVariable(ctx.text, ctx)
    }

    override fun visitNumberLiteral(ctx: NumberLiteralContext): Int? {
        return ctx.text.toInt()
    }

    override fun visitExpressionWithBraces(ctx: ExpressionWithBracesContext): Int? {
        return visit(ctx.expression())
    }

    override fun visitSingleExpression(ctx: SingleExpressionContext): Int? {
        return evaluate(ctx.children.first() as ParserRuleContext)
    }

    override fun visitOrExpression(ctx: OrExpressionContext): Int? {
        if (ctx.orExpression() == null) {
            return visit(ctx.andExpression())
        }
        return evaluateBinaryOperation(
                ctx.OR_OPERATION().text,
                evaluate(ctx.andExpression()),
                evaluate(ctx.orExpression()),
                ctx
        )
    }

    override fun visitAndExpression(ctx: AndExpressionContext): Int? {
        if (ctx.andExpression() == null) {
            return visit(ctx.compareExpression())
        }
        return evaluateBinaryOperation(
                ctx.AND_OPERATION().text,
                evaluate(ctx.compareExpression()),
                evaluate(ctx.andExpression()),
                ctx
        )
    }

    override fun visitCompareExpression(ctx: CompareExpressionContext): Int? {
        if (ctx.compareExpression() == null) {
            return visit(ctx.summingExpression())
        }
        return evaluateBinaryOperation(
                ctx.COMPARE_OPERATION().text,
                evaluate(ctx.summingExpression()),
                evaluate(ctx.compareExpression()),
                ctx
        )
    }

    override fun visitSummingExpression(ctx: SummingExpressionContext): Int? {
        if (ctx.summingExpression() == null) {
            return visit(ctx.multiplicatingExpression())
        }
        return evaluateBinaryOperation(
                ctx.SUMMING_OPERATION().text,
                evaluate(ctx.multiplicatingExpression()),
                evaluate(ctx.summingExpression()),
                ctx
        )
    }

    override fun visitMultiplicatingExpression(ctx: MultiplicatingExpressionContext): Int? {
        if (ctx.multiplicatingExpression() == null) {
            return visit(ctx.singleExpression())
        }
        return evaluateBinaryOperation(
                ctx.MULTIPLICATING_OPERATION().text,
                evaluate(ctx.singleExpression()),
                evaluate(ctx.multiplicatingExpression()),
                ctx
        )
    }

    private fun evaluate(ctx: ParserRuleContext): Int {
        return visit(ctx) ?: throw RuntimeException("Line ${ctx.start.line}: return value expected.")
    }

    private fun evaluateBinaryOperation(operation: String, first: Int, second: Int,
                                        ctx: ParserRuleContext): Int {
        when (operation) {
            "&&" -> return toInt(toBoolean(first) && toBoolean(second))
            "||" -> return toInt(toBoolean(first) || toBoolean(second))
            ">" -> return toInt(first > second)
            ">=" -> return toInt(first >= second)
            "<" -> return toInt(first < second)
            "<=" -> return toInt(first <= second)
            "==" -> return toInt(first == second)
            "!=" -> return toInt(first != second)
            "+" -> return first + second
            "-" -> return first - second
            "*" -> return first * second
            "/" -> return first / second
            "%" -> return first % second
        }
        throw RuntimeException("Line ${ctx.start.line}: invalid operation $operation")
    }

    private fun toBoolean(x: Int): Boolean {
        return x != 0
    }

    private fun toInt(x: Boolean): Int {
        if (x) {
            return 1
        }
        return 0
    }
}