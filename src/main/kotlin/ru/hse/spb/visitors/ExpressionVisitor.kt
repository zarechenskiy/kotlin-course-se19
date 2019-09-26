package ru.hse.spb.visitors

import ru.hse.spb.declarations.BinaryExpression
import ru.hse.spb.declarations.BinaryOperator
import ru.hse.spb.declarations.BinaryOperator.*
import ru.hse.spb.declarations.ExpressionDeclaration
import ru.hse.spb.declarations.FunctionCallExpression
import ru.hse.spb.declarations.NumberExpression
import ru.hse.spb.declarations.VariableExpression
import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunParser
import java.lang.RuntimeException

class ExpressionVisitor : FunBaseVisitor<ExpressionDeclaration>() {
    private fun toBinaryOperator(text: String): BinaryOperator {
        return when (text) {
            "*" -> MUL
            "/" -> DIV
            "%" -> MOD
            "+" -> PLUS
            "-" -> MINUS
            "<" -> LESS
            ">" -> GREATER
            "<=" -> LESS_EQUALS
            ">=" -> GREATER_EQUALS
            "==" -> EQUALS
            "!=" -> NOT_EQUALS
            "&&" -> AND
            "||" -> OR
            else -> throw RuntimeException("Invalid operator $text")
        }
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext): ExpressionDeclaration {
        if (ctx.simpleExpression() != null) {
            return visit(ctx.simpleExpression())
        }
        val lhs = visit(ctx.lhs)
        val rhs = visit(ctx.rhs)
        val op = toBinaryOperator(ctx.op.text)
        return BinaryExpression(lhs, rhs, op)
    }

    override fun visitSimpleExpression(ctx: FunParser.SimpleExpressionContext): ExpressionDeclaration {
        if (ctx.functionCall() != null) {
            return visit(ctx.functionCall())
        }
        if (ctx.IDENTIFIER() != null) {
            return VariableExpression(ctx.IDENTIFIER().text)
        }
        if (ctx.LITERAL() != null) {
            return NumberExpression(ctx.LITERAL().text.toInt())
        }
        return visit(ctx.expression())
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext): ExpressionDeclaration {
        val name = ctx.name.text
        val argumentNames = ctx.arguments().expression().map { visit(it) }
        return FunctionCallExpression(name, argumentNames)
    }
}
