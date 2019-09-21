package ru.hse.spb

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.FunLanguageVisitorImplementation.IdentifierInfo.FunctionInfo
import ru.hse.spb.FunLanguageVisitorImplementation.IdentifierInfo.VariableInfo
import ru.hse.spb.FunLanguageVisitorImplementation.InterpretingException
import ru.hse.spb.FunLanguageVisitorImplementation.NodeProcessingResult
import ru.hse.spb.parser.FunLanguageBaseVisitor
import ru.hse.spb.parser.FunLanguageLexer
import ru.hse.spb.parser.FunLanguageParser
import java.io.PrintWriter

/**
 * Accepts exactly one argument -- a path to file with source code.
 * Interprets the code and redirects program output to STDOUT.
 * The parsing is done with ANTLR 4.
 */
fun main(arguments: Array<String>) {
    if (arguments.size != 1) {
        println("Wrong number of parameters: the program accepts exactly one argument: name of the file to interpret")
        return
    }
    val fileName = arguments[0]
    val lexer = FunLanguageLexer(CharStreams.fromFileName(fileName))
    val parser = FunLanguageParser(CommonTokenStream(lexer))

    PrintWriter(System.out).use {
        try {
            parser.file().accept(FunLanguageVisitorImplementation(it))
        } catch (exception: InterpretingException) {
            it.println("ERROR: ${exception.message ?: "unknown error"}")
        }
    }

}

/**
 * This class is an implementation of FunLanguageVisitor interface (the interface is generated
 * by ANTLR 4), which is used to interpret code written in FunLanguage.
 */
class FunLanguageVisitorImplementation(private val out: PrintWriter) : FunLanguageBaseVisitor<NodeProcessingResult>() {
    private var currentScope: PersistentMap<String, IdentifierInfo> = persistentHashMapOf()

    override fun visitBlock(ctx: FunLanguageParser.BlockContext): NodeProcessingResult {
        for (statement in ctx.statement()) {
            val result = statement.accept(this)
            if (result.shouldReturn) {
                return result
            }
        }
        return VOID
    }

    override fun visitBlockWithBraces(ctx: FunLanguageParser.BlockWithBracesContext): NodeProcessingResult {
        return ctx.block().accept(this)
    }

    override fun visitFunction(ctx: FunLanguageParser.FunctionContext): NodeProcessingResult {
        val name = ctx.IDENTIFIER().text
        val parameters = ctx.parameterNames().IDENTIFIER().map { it.text }
        val functionInfo = FunctionInfo(ctx.blockWithBraces(), parameters, currentScope);
        currentScope = currentScope.put(name, functionInfo)
        functionInfo.scopeSnapshot = currentScope
        return VOID
    }

    override fun visitVariable(ctx: FunLanguageParser.VariableContext): NodeProcessingResult {
        val name = ctx.IDENTIFIER().text
        val value = ctx.expression()?.accept(this)
        currentScope = currentScope.put(name, VariableInfo(value?.value ?: 0))
        return VOID
    }

    override fun visitWhileStatement(ctx: FunLanguageParser.WhileStatementContext): NodeProcessingResult {
        while (ctx.expression().accept(this).value.toBoolean()) {
            val result = ctx.blockWithBraces().accept(this)
            if (result.shouldReturn) {
                return result
            }
        }
        return VOID
    }

    override fun visitIfStatement(ctx: FunLanguageParser.IfStatementContext): NodeProcessingResult {
        if (ctx.expression().accept(this).value.toBoolean()) {
            val result = ctx.blockWithBraces(0).accept(this)
            if (result.shouldReturn) {
                return result
            }
        } else {
            val result = ctx.blockWithBraces(1)?.accept(this)
            if (result?.shouldReturn == true) {
                return result
            }
        }
        return VOID
    }

    override fun visitAssignment(ctx: FunLanguageParser.AssignmentContext): NodeProcessingResult {
        val name = ctx.IDENTIFIER().text
        when (val info = currentScope[name]) {
            is VariableInfo -> info.value = ctx.expression().accept(this).value
            is FunctionInfo -> throw InterpretingException(getNameBelongsToFunctionMessage(ctx.start.line, name))
            else -> throw InterpretingException(getVariableDoesNotExistMessage(ctx.start.line, name))
        }
        return VOID
    }

    override fun visitReturnStatement(ctx: FunLanguageParser.ReturnStatementContext): NodeProcessingResult {
        return NodeProcessingResult(ctx.expression().accept(this).value, true)
    }

    override fun visitFunctionCall(ctx: FunLanguageParser.FunctionCallContext): NodeProcessingResult {
        val name = ctx.IDENTIFIER().text
        val identifierInfo = currentScope[name]

        when(identifierInfo) {
            is FunctionInfo -> {}
            is VariableInfo -> throw InterpretingException(getNameBelongsToVariableMessage(ctx.start.line, name))
            else -> {
                if (name == PRINTLN) {
                    return executePrintlnCall(ctx)
                }
                throw InterpretingException(getFunctionDoesNotExistMessage(ctx.start.line, name))
            }
        }

        if (identifierInfo.arguments.size != ctx.arguments().expression()?.size ?: 0) {
            throw InterpretingException(getWrongNumberOfParametersMessage(
                    ctx.start.line,
                    identifierInfo.arguments.size,
                    ctx.arguments().expression()?.size ?: 0
            ))
        }

        var functionCallScope = identifierInfo.scopeSnapshot
        for (i in identifierInfo.arguments.indices) {
            val value = ctx.arguments().expression(i).accept(this).value
            functionCallScope = functionCallScope.put(identifierInfo.arguments[i], VariableInfo(value))
        }

        val result = callWithScope(functionCallScope) { identifierInfo.blockWithBracesContext.accept(this) }
        return NodeProcessingResult(result.value, false)
    }

    private fun executePrintlnCall(ctx: FunLanguageParser.FunctionCallContext): NodeProcessingResult {
        if (ctx.arguments().expression().isEmpty()) {
            out.println()
            return VOID
        }
        for (argument in ctx.arguments().expression()) {
            out.println(argument.accept(this).value)
        }
        return VOID
    }

    private inline fun <R> callWithScope(scope: PersistentMap<String, IdentifierInfo> , supplier: () -> R): R {
        val savedScope = currentScope
        currentScope = scope
        val result = supplier()
        currentScope = savedScope
        return result
    }

    override fun visitSimpleExpression(ctx: FunLanguageParser.SimpleExpressionContext): NodeProcessingResult {
        ctx.LITERAL()?.let {
            val value = ctx.LITERAL().text.toIntOrNull()
                    ?: throw InterpretingException(getBadLiteralMessage(ctx.start.line, ctx.LITERAL().text))
            return NodeProcessingResult(value, false)
        }

        return ctx.functionCall()?.accept(this)
                ?: ctx.expression()?.accept(this)
                ?: NodeProcessingResult(getVariableValue(ctx.start.line, ctx.IDENTIFIER().text), false)
    }

    private fun getVariableValue(line: Int, name: String): Int {
        val variableInfo = currentScope[name]
        when(variableInfo) {
            is VariableInfo -> {}
            is FunctionInfo -> throw InterpretingException(getNameBelongsToFunctionMessage(line, name))
            else -> throw InterpretingException(getVariableDoesNotExistMessage(line, name))
        }
        return variableInfo.value
    }

    override fun visitExpression(ctx: FunLanguageParser.ExpressionContext): NodeProcessingResult {
        ctx.simpleExpression()?.let {
            return it.accept(this)
        }
        val left = ctx.expression(0).accept(this).value
        val right = ctx.expression(1).accept(this).value
        val result =
                ctx.MULTIPLICATION()?.let   { left * right }
                ?: ctx.DIVISION()?.let      { left / right  }
                ?: ctx.ADDITION()?.let      { left + right  }
                ?: ctx.SUBSTRACTION()?.let  { left - right  }
                ?: ctx.REMINDER()?.let      { left % right  }
                ?: ctx.LT()?.let            { (left < right).toInt() }
                ?: ctx.LTE()?.let           { (left <= right).toInt() }
                ?: ctx.GT()?.let            { (left > right).toInt()  }
                ?: ctx.GTE()?.let           { (left >= right).toInt() }
                ?: ctx.EQ()?.let            { (left == right).toInt() }
                ?: ctx.NEQ()?.let           { (left != right).toInt() }
                ?: ctx.LOGIC_AND()?.let     { (left.toBoolean() && right.toBoolean()).toInt() }
                ?: ctx.LOGIC_OR().let       { (left.toBoolean() || right.toBoolean()).toInt() }
        return NodeProcessingResult(result, false)
    }

    private fun Boolean.toInt(): Int = if (this) 1 else 0

    private fun Int.toBoolean(): Boolean = this != 0

    internal sealed class IdentifierInfo {
        internal data class VariableInfo(var value: Int) : IdentifierInfo()
        internal data class FunctionInfo(val blockWithBracesContext: FunLanguageParser.BlockWithBracesContext,
                                         val arguments: List<String>,
                                         var scopeSnapshot: PersistentMap<String, IdentifierInfo>) : IdentifierInfo()
    }

    data class NodeProcessingResult(val value: Int, val shouldReturn: Boolean)

    private companion object {
        private val VOID = NodeProcessingResult(0, false)
        private const val PRINTLN = "println"

        private fun getNameBelongsToFunctionMessage(line: Int, name: String) =
                "$line: name '$name' belongs to a function"

        private fun getNameBelongsToVariableMessage(line: Int, name: String) =
                "$line: name '$name' belongs to a variable"

        private fun getVariableDoesNotExistMessage(line: Int, name: String) =
                "$line: variable '$name' does not exist"

        private fun getFunctionDoesNotExistMessage(line: Int, name: String) =
                "$line: function '$name' not found"

        private fun getWrongNumberOfParametersMessage(line: Int, required: Int, provided: Int) =
                "$line: wrong number of parameters. Required: $required; provided: $provided"

        private fun getBadLiteralMessage(line: Int, literal: String) =
                "$line: number '$literal' could not be represented by Int type"
    }

    class InterpretingException(message: String) : RuntimeException(message)
}