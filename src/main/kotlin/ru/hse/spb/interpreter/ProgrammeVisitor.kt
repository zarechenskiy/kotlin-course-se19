package ru.hse.spb.interpreter

import ru.hse.spb.parser.ProgrammeBaseVisitor
import ru.hse.spb.parser.ProgrammeParser.*
import kotlin.streams.toList

private const val ZERO = 0

class ProgrammeVisitor : ProgrammeBaseVisitor<Int>() {
    var exit: Boolean = false

    var scope: Scope = Scope(null)

    override fun visitFile(ctx: FileContext): Int {
        return ctx.block().accept(this)
    }

    override fun visitBlock(ctx: BlockContext): Int {
        var returnValue = ZERO
        for (statement in ctx.statements) {
            returnValue = statement.accept(this)
            if (exit) {
                return returnValue
            }
        }
        return returnValue
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext): Int {
        scope = Scope(scope)
        val returnValue = ctx.block().accept(this)
        scope = scope.parentScope!!
        return returnValue
    }

    override fun visitStatement(ctx: StatementContext): Int {
        return ctx.function()?.accept(this)
                ?: ctx.variable()?.accept(this)
                ?: ctx.expression()?.accept(this)
                ?: ctx.blockWhile()?.accept(this)
                ?: ctx.brockIf()?.accept(this)
                ?: ctx.assigment()?.accept(this)
                ?: ctx.returnStatement()?.accept(this)
                ?: error("Unexpected statement ${ctx.start.text}")
    }

    override fun visitVariable(ctx: VariableContext): Int {
        val variableName = ctx.IDENTIFIER().text
        if (scope.containsLocalVariable(variableName)) {
            error("Double declaration of variable $variableName")
        }
        scope.addVariable(variableName, ctx.expression().accept(this))
        return ZERO
    }

    override fun visitFunction(ctx: FunctionContext): Int {
        val functionName = ctx.IDENTIFIER().text
        if (scope.containsLocalFunction(functionName)) {
            error("Double declaration of function $functionName")
        }
        scope.addFunction(functionName, ctx)
        return ZERO
    }

    override fun visitBlockWhile(ctx: BlockWhileContext): Int {
        var returnValue = ZERO
        while (ctx.expression().accept(this) != 0 && !exit) {
            returnValue = ctx.whileBlockWithBraces.accept(this)
        }
        return returnValue
    }

    override fun visitBrockIf(ctx: BrockIfContext): Int {
        return when (ctx.expression().accept(this) != 0) {
            true -> ctx.ifBlockWithBraces.accept(this)
            false -> ctx.elseBlockWithBraces?.accept(this) ?: ZERO
        }
    }

    override fun visitAssigment(ctx: AssigmentContext): Int {
        val variableName = ctx.IDENTIFIER().text
        if (!scope.recursiveContainsVariable(variableName)) {
            error("No such variable $variableName in scope")
        }
        scope.setVariable(variableName, ctx.expression().accept(this))
        return ZERO
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): Int {
        val returnValue = ctx.expression().accept(this)
        exit = true
        return returnValue
    }

    fun acceptFunctionBlockWithBraces(ctx: BlockWithBracesContext, inScope: Scope, outScope: Scope): Int {
        scope = inScope
        val returnValue = ctx.block().accept(this)
        scope = outScope
        return returnValue
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): Int {
        val functionName = ctx.IDENTIFIER().text
        val argValues = ctx.arguments.stream().map { arg -> arg.accept(this) }.toList()

        if (functionName == "println") {
            println(argValues.joinToString(" "))
            return ZERO
        }

        val (fctx, fscope) = scope.getFunction(functionName) ?: error("No function $functionName in scope")

        val argNames = fctx.parameters().parameterNames.stream().map { param -> param.text }.toList()

        if (argNames.size != argValues.size) {
            error("Wrong parameter number. Expected ${argNames.size}, but got ${argValues.size}")
        }

        val inScope = Scope(fscope)
        inScope.addVariables(argNames, argValues)
        val returnValue = acceptFunctionBlockWithBraces(fctx.blockWithBraces(), inScope, scope)

        exit = false
        return returnValue
    }

    override fun visitExpression(ctx: ExpressionContext): Int {
        return ctx.binaryExpression().accept(this)
    }

    override fun visitBinaryExpression(ctx: BinaryExpressionContext): Int {
        return ctx.logicalOrExp().accept(this)
    }

    override fun visitLogicalOrExp(ctx: LogicalOrExpContext): Int {
        return ctx.logicalAndExp()
                .map { logicalAndExpContext -> logicalAndExpContext.accept(this) }
                .fold(0) { acc, value -> acc + value }
    }

    override fun visitLogicalAndExp(ctx: LogicalAndExpContext): Int {
        return ctx.equalityExp()
                .map { equalityExp -> equalityExp.accept(this) }
                .fold(1) { acc, value -> acc * value }
    }

    fun Boolean.toInt() = if (this) 1 else 0

    override fun visitEqualityExp(ctx: EqualityExpContext): Int {
        return when(ctx.OP_EQUAL()?.text) {
            "==" -> (ctx.relationalExp(0).accept(this) == ctx.relationalExp(1).accept(this)).toInt()
            "!=" -> (ctx.relationalExp(0).accept(this) != ctx.relationalExp(1).accept(this)).toInt()
            else -> ctx.relationalExp(0).accept(this)
        }
    }

    override fun visitRelationalExp(ctx: RelationalExpContext): Int {
        return when(ctx.OP_COMPARE()?.text) {
            "<" -> (ctx.additionExp(0).accept(this) < ctx.additionExp(1).accept(this)).toInt()
            ">" -> (ctx.additionExp(0).accept(this) > ctx.additionExp(1).accept(this)).toInt()
            "<=" -> (ctx.additionExp(0).accept(this) <= ctx.additionExp(1).accept(this)).toInt()
            ">=" -> (ctx.additionExp(0).accept(this) >= ctx.additionExp(1).accept(this)).toInt()
            else -> ctx.additionExp(0).accept(this)
        }
    }

    override fun visitAdditionExp(ctx: AdditionExpContext): Int {
        return ctx.multiplyExp()
                .map { multiplyExpContext -> multiplyExpContext.accept(this) }
                .foldIndexed(0) { i, acc, value ->
                    when(ctx.OP_ADD().getOrNull(i - 1)?.text) {
                        "+" -> acc + value
                        "-" -> acc - value
                        else -> value
                    }
                }
    }

    override fun visitMultiplyExp(ctx: MultiplyExpContext): Int {
        return ctx.atomExp()
                .map { atomExpContext -> atomExpContext.accept(this) }
                .foldIndexed(1) { i, acc, value ->
                    when(ctx.OP_MULT().getOrNull(i - 1)?.text) {
                        "*" -> acc * value
                        "/" -> acc / value
                        "%" -> acc % value
                        else -> value
                    }
                }
    }

    override fun visitAtomExp(ctx: AtomExpContext): Int {
        return ctx.literal()?.accept(this)
                ?: ctx.identifier()?.accept(this)
                ?: ctx.expression()?.accept(this)
                ?: ctx.functionCall()?.accept(this)
                ?: error("Unexpected atom expression ${ctx.text}")
    }

    override fun visitIdentifier(ctx: IdentifierContext): Int {
        val variableName = ctx.IDENTIFIER().text
        return scope.getVariable(variableName)
                ?: error("No such variable $variableName in scope")
    }

    override fun visitLiteral(ctx: LiteralContext): Int {
        return ctx.LITERAL().text.toInt()
    }
}