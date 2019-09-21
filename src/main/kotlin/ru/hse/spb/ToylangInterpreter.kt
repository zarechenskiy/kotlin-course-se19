package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.ToylangBaseVisitor
import ru.hse.spb.parser.ToylangParser

typealias IntegralType = Int
typealias FunctionParameterType = List<IntegralType>
typealias FunctionType = (FunctionParameterType) -> IntegralType

class ToylangInterpreter(builtInFunctions: List<Pair<String, FunctionType>>) : ToylangBaseVisitor<Any>() {

    override fun visitStmtReturn(ctx: ToylangParser.StmtReturnContext) {
        val value = ctx.stmtExpression().accept(this).cast<IntegralType>()
        setReturnValue(value)
    }

    override fun visitIntegralLiteral(ctx: ToylangParser.IntegralLiteralContext): IntegralType =
        ctx.INTEGER_LITERAL().text.toInt()


    override fun visitIdentifier(ctx: ToylangParser.IdentifierContext): IntegralType {
        val name = ctx.IDENTIFIER().text
        return lookupVarDeeply(name).let {
            when (it) {
                null -> error("${ctx.lineString()} Variable not declared: $name")
                else -> it
            }
        }
    }

    override fun visitMultiplyExpression(ctx: ToylangParser.MultiplyExpressionContext): IntegralType {
        val op = ctx.OP_MULTIPLY().iterator()
        return ctx.atomExpression().map { it.accept(this).cast<IntegralType>() }.fold1 { acc, prev ->
            when (op.next().text) {
                "*" -> acc * prev
                "/" -> acc / prev
                "%" -> acc % prev
                else -> error("${ctx.lineString()} Unknown operation: ${op.next().text}")
            }
        }
    }

    override fun visitAdditionExpression(ctx: ToylangParser.AdditionExpressionContext): IntegralType {
        val op = ctx.OP_ADDITIONAL().iterator()
        return ctx.multiplyExpression().map { it.accept(this).cast<IntegralType>() }.fold1 { acc, prev ->
            when (op.next().text) {
                "+" -> acc + prev
                "-" -> acc - prev
                else -> error("${ctx.lineString()} Unknown operation: ${op.next().text}")
            }
        }
    }

    override fun visitCompareExpression(ctx: ToylangParser.CompareExpressionContext): IntegralType {
        val op = ctx.OP_COMPARE().iterator()
        return ctx.additionExpression().map { it.accept(this).cast<IntegralType>() }.fold1 { acc, prev ->
            when (op.next().text) {
                "<" -> (acc < prev).toIntegral()
                ">" -> (acc > prev).toIntegral()
                "<=" -> (acc <= prev).toIntegral()
                ">=" -> (acc >= prev).toIntegral()
                else -> error("${ctx.lineString()} Unknown operation: ${op.next().text}")
            }
        }
    }

    override fun visitEqExpression(ctx: ToylangParser.EqExpressionContext): IntegralType {
        val op = ctx.OP_EQ().iterator()
        return ctx.compareExpression().map { it.accept(this).cast<IntegralType>() }.fold1 { acc, prev ->
            when (op.next().text) {
                "==" -> (acc == prev).toIntegral()
                "!=" -> (acc != prev).toIntegral()
                else -> error("${ctx.lineString()} Unknown operation: ${op.next().text}")
            }
        }
    }

    override fun visitLogicalExpression(ctx: ToylangParser.LogicalExpressionContext): IntegralType {
        val op = ctx.OP_LOGICAL().iterator()
        return ctx.eqExpression().map { it.accept(this).cast<IntegralType>() }
            .fold1 { acc, prev ->
                when (op.next().text) {
                    "||" -> (acc.toBoolean() || prev.toBoolean()).toIntegral()
                    "&&" -> (acc.toBoolean() && prev.toBoolean()).toIntegral()
                    else -> error("${ctx.lineString()} Unknown operation: ${op.next().text}")
                }
            }
    }


    override fun visitFunctionArguments(ctx: ToylangParser.FunctionArgumentsContext): List<IntegralType> {
        return ctx.stmtExpression().map { it.accept(this).cast<IntegralType>() }
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitFunctionCall(ctx: ToylangParser.FunctionCallContext): IntegralType {
        val name = ctx.IDENTIFIER().text
        val args = ctx.functionArguments().accept(this)
        return lookupFunDeeply(name).let {
            when (it) {
                null -> error("${ctx.lineString()} Function not declared: $name")
                else -> it(args as List<IntegralType>)
            }
        }
    }

    override fun visitStmtAssigment(ctx: ToylangParser.StmtAssigmentContext) {
        val name = ctx.IDENTIFIER().text
        when (lookupVarDeeply(name)) {
            null -> error("${ctx.lineString()} Variable not declared: $name")
            else -> updateVariable(name, ctx.stmtExpression().accept(this).cast())
        }
    }

    override fun visitStmtIf(ctx: ToylangParser.StmtIfContext) {
        if (ctx.stmtExpression().accept(this).cast<IntegralType>() != 0) {
            ctx.branchThen.accept(this)
        } else if (ctx.KW_ELSE() != null) {
            ctx.branchElse.accept(this)
        }
    }

    override fun visitStmtWhile(ctx: ToylangParser.StmtWhileContext) {
        while (ctx.stmtExpression().accept(this).cast<IntegralType>() != 0) {
            ctx.blockWithBraces().accept(this)
        }
    }

    override fun visitFunctionParameterNames(ctx: ToylangParser.FunctionParameterNamesContext): List<String> =
        ctx.parameters.map { it.text }

    @Suppress("UNCHECKED_CAST")
    override fun visitStmtFunction(ctx: ToylangParser.StmtFunctionContext) {
        val name = ctx.IDENTIFIER().text
        when (lookupFunDeeply(name)) {
            null -> putFun(
                name,
                FunctionEvaluator(ctx.functionParameterNames().accept(this) as List<String>, ctx.blockWithBraces())
            )
            else -> error("${ctx.lineString()} Function already declared: $name")
        }
    }

    override fun visitStmtVariable(ctx: ToylangParser.StmtVariableContext) {
        val name = ctx.IDENTIFIER().text
        when (lookupVar(name)) {
            null -> putVar(
                name, when (ctx.stmtExpression()) {
                    null -> defaultValue
                    else -> ctx.stmtExpression().accept(this).cast()
                }
            )
            else -> error("${ctx.lineString()} Variable already declared: $name")
        }
    }

    override fun visitBlockWithBraces(ctx: ToylangParser.BlockWithBracesContext): Any =
        withNewStackframe { ctx.block().accept(this) }

    override fun visitBlock(ctx: ToylangParser.BlockContext): IntegralType {
        val statements = ctx.statement().iterator()
        while (getReturnValue() == null && statements.hasNext()) {
            statements.next().accept(this)
        }
        return getReturnValue() ?: defaultValue
    }

    override fun visitFile(ctx: ToylangParser.FileContext): Any =
        ctx.block().accept(this)


    private inner class FunctionEvaluator(
        private val functionParameterNames: List<String>,
        private val functionBody: ToylangParser.BlockWithBracesContext
    ) : FunctionType {
        override fun invoke(args: List<IntegralType>): IntegralType {
            if (functionParameterNames.size != args.size) error("${functionBody.lineString()} Function argument count mismatched")

            return withNewStackframe(isFunction = true) {
                functionParameterNames.zip(args).forEach { putVar(it.first, it.second) }
                functionBody.block().accept(this@ToylangInterpreter)
            }.cast()
        }
    }

    private val interpreterState: InterpreterState<IntegralType, FunctionType> = ToylangInterpreterState()
    private val defaultValue = 0

    private inline fun <reified T> withNewStackframe(isFunction: Boolean = false, block: () -> T): T {
        interpreterState.addFrame(isFunction)
        val result = block()
        interpreterState.popFrame()
        return result
    }

    private fun setReturnValue(value: IntegralType): Boolean = interpreterState.setReturnValue(value)
    private fun getReturnValue(): IntegralType? = interpreterState.getReturnValue()

    private fun putVar(name: String, value: IntegralType) = interpreterState.putVariableInLastFrame(name, value)
    fun lookupVar(name: String) = interpreterState.lookupVariableInLastFrame(name)
    private fun lookupVarDeeply(name: String) = interpreterState.lookupVariable(name)
    private fun updateVariable(name: String, value: IntegralType) = interpreterState.updateVariable(name, value)

    private fun putFun(name: String, value: FunctionType) = interpreterState.putFunctionInLastFrame(name, value)
    private fun lookupFunDeeply(name: String) = interpreterState.lookupFunction(name)
    fun lookupFun(name: String) = interpreterState.lookupFunctionInLastFrame(name)

    init {
        builtInFunctions.forEach { putFun(it.first, it.second) }
    }
}

private fun IntegralType.toBoolean(): Boolean {
    return this != 0
}

private fun Boolean.toIntegral(): IntegralType {
    return when (this) {
        false -> 0
        else -> 1
    }
}

private inline fun <T> Iterable<T>.fold1(operation: (acc: T, T) -> T): T {
    val iterator = this.iterator()
    var accumulator = iterator.next()
    while (iterator.hasNext()) accumulator = operation(accumulator, iterator.next())
    return accumulator
}

private fun ParserRuleContext.lineString(): String {
    return "Line ${this.start.line}."
}

private inline fun <reified T> Any.cast(): T {
    when (this) {
        is T -> return this
        else -> error("Expression must be of type: ${T::class.simpleName}")
    }
}