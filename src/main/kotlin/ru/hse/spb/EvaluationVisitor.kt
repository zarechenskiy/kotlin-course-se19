package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.GrammarBaseVisitor
import ru.hse.spb.parser.GrammarParser
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

/** Evaluate value of tokens. Every accept method returns int value. Zero byb default. */
class EvaluationVisitor(private var scope: Scope = Scope()) : GrammarBaseVisitor<Int>() {

    /** Flag is true if return was called, so result should be returned from nearest function call. */
    private var isReturnCalled = false

    /** Evaluate file value. */
    override fun visitFile(ctx: GrammarParser.FileContext): Int {
        isReturnCalled = false
        return ctx.block().accept(this)
    }

    /**
     * Evaluate block with braces value.
     * Every block creates it's own scope of functions and variables.
     */
    override fun visitBlockWithBraces(ctx: GrammarParser.BlockWithBracesContext): Int {
        scope = Scope(scope)
        val result = ctx.block().accept(this)
        scope = scope.parent!!
        return result
    }

    /** Evaluate block value -- the return value. */
    override fun visitBlock(ctx: GrammarParser.BlockContext): Int {
        for (statement in ctx.statements) {
            val result = statement.accept(this)
            if (isReturnCalled) {
                return result
            }
        }
        return 0
    }

    /** Evaluate statement value. */
    override fun visitStatement(ctx: GrammarParser.StatementContext): Int {
        return ctx.assignment()?.accept(this)
            ?: ctx.expression()?.accept(this)
            ?: ctx.function()?.accept(this)
            ?: ctx.ifStatement()?.accept(this)
            ?: ctx.variableDefinition()?.accept(this)
            ?: ctx.returnStatement()?.accept(this)
            ?: ctx.whileStatement()?.accept(this)
            ?: error("${ctx.line()}: Unexpected statement.")
    }

    /**
     * Evaluate variable definition value.
     * Always return zero.
     * Throws if variable with the same name has been defined before in this scope.
     */
    override fun visitVariableDefinition(ctx: GrammarParser.VariableDefinitionContext): Int {
        val variableName = ctx.variableName()
        if (scope.containsVariableInCurrentScope(variableName)) {
            error("${ctx.line()}: Variable redefinition: $variableName.")
        }
        val value: Int? = ctx.expression()?.accept(this)
        scope.putVariable(variableName, value)
        return 0
    }

    /**
     * Evaluate variable value. Throws if variable is undefined.
     * Throws if no value has been defined in definition.
     */
    override fun visitVariable(ctx: GrammarParser.VariableContext): Int {
        val variableName = ctx.variableName()
        if (!scope.containsVariable(variableName)) {
            error("${ctx.line()}: No such variable: $variableName in scope.")
        }
        return scope.getVariable(variableName)
            ?: error("${ctx.line()}: Variable value has not been specified: $variableName.")
    }

    /** Create function in scope. Returns zero. Throws on function redefinition. */
    override fun visitFunction(ctx: GrammarParser.FunctionContext): Int {
        val functionName = ctx.functionName()
        if (scope.containsFunctionInCurrentScope(functionName)) {
            error("${ctx.line()}: Function redefinition: $functionName.")
        }
        scope.putFunction(functionName, FunctionScope(ctx, scope))
        return 0
    }

    /** Evaluate assignment. Throws on undefined variable. Returns zero. */
    override fun visitAssignment(ctx: GrammarParser.AssignmentContext): Int {
        val variableName = ctx.variable().variableName()
        if (!scope.containsVariable(variableName)) {
            error("${ctx.line()}: Undefined variable: $variableName.")
        }
        val value = ctx.expression().accept(this)
        scope.setVariable(variableName, value)
        return 0
    }

    /** Evaluate function call. Throws on undefined function, illegal arguments number. */
    override fun visitFunctionCall(ctx: GrammarParser.FunctionCallContext): Int {
        val functionName = ctx.functionName()
        val arguments = ctx.arguments()?.accept(ArgumentsVisitor(this))
            ?: emptyList()

        if (!scope.containsFunction(functionName)) {
            if (BuiltIns.containsFunction(functionName)) {
                val builtInFunction = BuiltIns.functions[functionName]
                builtInFunction!!.call(arguments.toTypedArray())
                return 0
            }
            error("${ctx.line()}: No such function: $functionName.")
        }
        val (functionContext, functionScope) = scope.getFunction(functionName)!!
        val functionCallScope = Scope(functionScope)
        val argumentNames = functionContext.parameterNames().accept(ParameterNamesVisitor())
        if (argumentNames.size != arguments.size) {
            error("${ctx.line()}: Illegal arguments number: expected ${argumentNames.size}, but ${arguments.size} found.")
        }
        for ((name, value) in argumentNames zip arguments) {
            functionCallScope.putVariable(name, value)
        }
        val value = functionContext.blockWithBraces().accept(EvaluationVisitor(functionCallScope))
        isReturnCalled = false
        return value
    }

    /** Execute is statement. Returns block result. */
    override fun visitIfStatement(ctx: GrammarParser.IfStatementContext): Int {
        val condition = ctx.expression().accept(this)
        if (condition != 0) {
            return ctx.ifBlock.accept(this)
        }
        return ctx.elseBlock?.accept(this) ?: 0
    }

    /** Execute while statement. Returns block result. */
    override fun visitWhileStatement(ctx: GrammarParser.WhileStatementContext): Int {
        var condition = ctx.expression().accept(this)
        while (condition != 0) {
            val value = ctx.blockWithBraces().accept(this)
            if (isReturnCalled) {
                return value
            }
            condition = ctx.expression().accept(this)
        }
        return 0
    }

    /** Execute return. Returns return value. */
    override fun visitReturnStatement(ctx: GrammarParser.ReturnStatementContext): Int {
        val value = ctx.expression().accept(this)
        isReturnCalled = true
        return value
    }

    override fun visitPrimaryExpression(ctx: GrammarParser.PrimaryExpressionContext): Int {
        return ctx.functionCall()?.accept(this)
            ?: ctx.number()?.accept(this)
            ?: ctx.expression()?.accept(this)
            ?: ctx.variable()?.accept(this)
            ?: error("${ctx.line()}: Error while parsing.")
    }

    override fun visitExpression(ctx: GrammarParser.ExpressionContext): Int {
        return ctx.binaryExpression().accept(this)
    }

    override fun visitBinaryExpression(ctx: GrammarParser.BinaryExpressionContext): Int {
        return ctx.logicalOrExpression().accept(this)
    }

    override fun visitLogicalOrExpression(ctx: GrammarParser.LogicalOrExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: booleanToInt(ctx.left.accept(this) != 0
                || ctx.right.accept(this) != 0)
    }

    override fun visitLogicalAndExpression(ctx: GrammarParser.LogicalAndExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: booleanToInt(ctx.left.accept(this) != 0
                && ctx.right.accept(this) != 0)
    }

    override fun visitEqualityExpression(ctx: GrammarParser.EqualityExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: let {
                val left = ctx.left.accept(this)
                val right = ctx.right.accept(this)
                return booleanToInt(ctx.equalityOperation().EQ() != null && left == right
                        || ctx.equalityOperation().NEQ() != null && left != right)
            }
    }

    override fun visitRelationalExpression(ctx: GrammarParser.RelationalExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: let {
                val left = ctx.left.accept(this)
                val right = ctx.right.accept(this)
                val operation = ctx.relationalOperation()
                val result: Boolean = when {
                    operation.GE() != null -> left >= right
                    operation.GT() != null -> left > right
                    operation.LE() != null -> left <= right
                    else -> left < right
                }
                return booleanToInt(result)
            }
    }

    override fun visitAdditiveExpression(ctx: GrammarParser.AdditiveExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: let {
                val left = ctx.left.accept(this)
                val right = ctx.right.accept(this)
                val operation = ctx.additiveOperation()
                if (operation.PLUS() != null) {
                    left + right
                } else {
                    left - right
                }
            }
    }

    override fun visitMultiplicativeExpression(ctx: GrammarParser.MultiplicativeExpressionContext): Int {
        return ctx.inside?.accept(this)
            ?: let {
                val left = ctx.left.accept(this)
                val right = ctx.right.accept(this)
                val operation = ctx.multiplicativeOperation()
                when {
                    operation.DIV() != null -> left / right
                    operation.MOD() != null -> left % right
                    else -> left * right
                }
            }
    }

    override fun visitNumber(ctx: GrammarParser.NumberContext): Int {
        return ctx.NUMBER_LITERAL().text.toIntOrNull()
            ?: error("${ctx.line()}: Not an integer.")
    }
}

/** Get function parameter names. */
class ParameterNamesVisitor: GrammarBaseVisitor<List<String>>() {
    override fun visitParameterNames(ctx: GrammarParser.ParameterNamesContext): List<String> {
        return ctx.parameterNamesList.map { it.text }
    }
}

/** Get function call parameter values. */
class ArgumentsVisitor(private val visitor: EvaluationVisitor): GrammarBaseVisitor<List<Int>>() {
    override fun visitArguments(ctx: GrammarParser.ArgumentsContext): List<Int> {
        return ctx.argumentList.map { it.accept(visitor) }
    }
}

/** Built in functions and constants. */
private object BuiltIns {
    object Functions {

        /** Prints arguments separated with space on a new line. */
        @Suppress("unused")
        fun println(vararg arguments: Any?) {
            for (argument in arguments) {
                print(argument)
                print(' ')
            }
            print('\n')
        }
    }

    class BuiltInFunction(private val kFunction: KFunction<*>) {
        fun call(vararg arguments: Any?) {
            kFunction.call(Functions, *arguments)
        }
    }

    val functions = Functions::class
        .declaredMemberFunctions
        .associate { it.name to BuiltInFunction(it) }

    fun containsFunction(name: String): Boolean {
        return functions.containsKey(name)
    }

}

private fun GrammarParser.VariableContext.variableName() = IDENTIFIER().text
private fun GrammarParser.VariableDefinitionContext.variableName() = IDENTIFIER().text
private fun GrammarParser.FunctionCallContext.functionName() = IDENTIFIER().text
private fun GrammarParser.FunctionContext.functionName() = IDENTIFIER().text
private fun booleanToInt(condition: Boolean) = if (condition) 1 else 0

private fun ParserRuleContext.line() = start.line
