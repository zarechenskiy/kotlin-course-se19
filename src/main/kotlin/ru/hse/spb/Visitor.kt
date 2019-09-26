package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser.*

/**
 * Exception during compilation of the given input file
 */
class CompileException(context: ParserRuleContext, message: String) : RuntimeException("Line ${context.start.line}: " + message)

/**
 * Rules for visiting all AST nodes of the input file
 */
class Visitor(private var scopes: Scopes) : ExpBaseVisitor<Int?>() {
    private var returnCalled = false
    private var returnValue: Int? = null

    override fun visitFile(context: FileContext): Int? {
        return visit(context.block())
    }

    override fun visitBlock(context: BlockContext): Int? {
        for (statement in context.statement()) {
            if (returnCalled) {
                return null
            }

            visit(statement)
        }

        return null
    }

    override fun visitBlockWithBraces(context: BlockWithBracesContext): Int? {
        val oldScopes = scopes
        scopes = Scopes(oldScopes)
        visit(context.block())
        scopes = oldScopes

        return null
    }

    override fun visitStatement(context: StatementContext): Int? {
        return visit(context.children.first())
    }

    override fun visitFunction(context: FunctionContext): Int? {
        val name = context.identifier().IDENTIFIER().text
        val argumentNames = context.parameterNames().identifier().map {it.IDENTIFIER().text}
        scopes.functionsScope.insert(name, {args ->
            if (argumentNames.size != args.size) {
                throw CompileException(context, "wrong number of parameters")
            }

            val oldScopes = scopes
            scopes = Scopes(oldScopes)

            for (i in 0..argumentNames.lastIndex) {
                scopes.variablesScope.insert(argumentNames[i], args[i], context)
            }

            visit(context.blockWithBraces())
            scopes = oldScopes

            returnCalled = false
            returnValue ?: 0
        }, context)

        return null
    }

    override fun visitVariable(context: VariableContext): Int? {
        var value: Int? = null
        if (context.expression() != null) {
            value = visit(context.expression())
        }
        scopes.variablesScope.insert(context.identifier().IDENTIFIER().text, value, context)
        return null
    }

    override fun visitWhileStatement(context: WhileStatementContext): Int? {
        while (toBoolean(visit(context.expression()))) {
            visit(context.blockWithBraces())

            if (returnCalled) {
                return null
            }
        }

        return null
    }

    override fun visitFunctionCall(context: FunctionCallContext): Int? {
        val function = scopes.functionsScope.getScoped(context.identifier().IDENTIFIER().text, context)
        val arguments = context.arguments().expression().map{visit(it)}

        if (function == null) {
            throw CompileException(context, "unknown function")
        }

        return function(arguments)
    }

    override fun visitIfStatement(context: IfStatementContext): Int? {
        if (toBoolean(visit(context.expression()))) {
            return visit(context.blockWithBraces().first())
        } else if (context.blockWithBraces().size > 1) {
            return visit(context.blockWithBraces().last())
        }

        return null
    }

    override fun visitAssigment(context: AssigmentContext): Int? {
        scopes.variablesScope.update(context.identifier().IDENTIFIER().text, visit(context.expression()), context)
        return null
    }

    override fun visitReturnStatement(context: ReturnStatementContext): Int? {
        val value = visit(context.expression())
        returnValue = value
        returnCalled = true
        return null
    }

    override fun visitExpression(context: ExpressionContext): Int? {
        return visit(context.binaryExpression())
    }

    override fun visitNonBinaryExpression(context: NonBinaryExpressionContext): Int? {
        if (context.functionCall() != null) {
            return visit(context.functionCall())
        } else if (context.identifier() != null) {
            return scopes.variablesScope.getScoped(context.identifier().IDENTIFIER().text, context)
        } else if (context.literal() != null) {
            return context.literal().LITERAL().text.toInt()
        } else {
            return visit(context.expression())
        }
    }

    override fun visitBinaryExpression(context: BinaryExpressionContext): Int? {
        if (context.nonBinaryExpression() != null) {
            return visitNonBinaryExpression(context.nonBinaryExpression())
        } else {
            val left = visitBinaryExpression(context.binaryExpression(0))
            val right = visitBinaryExpression(context.binaryExpression(1))

            if (left == null || right == null) {
                throw CompileException(context, "Values in binary expressions cannot be null")
            }

            var operator = context.operatorFirstLevel()?.OPERATOR_FIRST_LEVEL()

            if (operator == null) {
                if (context.operatorSecondLevel() != null) {
                    operator = context.operatorSecondLevel().OPERATOR_SECOND_LEVEL()
                } else if (context.operatorThirdLevel() != null) {
                    operator = context.operatorThirdLevel().OPERATOR_THIRD_LEVEL()
                } else if (context.operatorFourthLevel() != null) {
                    operator = context.operatorFourthLevel().OPERATOR_FOURTH_LEVEL()
                } else if (context.operatorFifthLevel() != null) {
                    operator = context.operatorFifthLevel().OPERATOR_FIFTH_LEVEL()
                } else {
                    throw CompileException(context, "Unknown binary operator priority")
                }
            }

            return when (operator?.text) {
                "+" -> left + right
                "-" -> left - right
                "*" -> left * right
                "/" -> left / right
                "%" -> left % right
                ">" -> toInt(left > right)
                "<" -> toInt(left < right)
                ">=" -> toInt(left >= right)
                "<=" -> toInt(left <= right)
                "==" -> toInt(left == right)
                "!=" -> toInt(left != right)
                "||" -> toInt(toBoolean(left) || toBoolean(right))
                "&&" -> toInt(toBoolean(left) && toBoolean(right))
                else -> throw RuntimeException("Unknown binary operator")
            }
        }
    }

    private fun toBoolean(x: Int?): Boolean {
        return x != 0
    }

    private fun toInt(x: Boolean?): Int {
        if (x == null) {
            return 0
        }

        if (x) {
            return 1
        }

        return 0
    }
}