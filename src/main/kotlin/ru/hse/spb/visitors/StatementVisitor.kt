package ru.hse.spb.visitors

import ru.hse.spb.declarations.AssignmentStatement
import ru.hse.spb.declarations.CustomFunctionStatement
import ru.hse.spb.declarations.CycleStatement
import ru.hse.spb.declarations.IfStatement
import ru.hse.spb.declarations.ReturnStatement
import ru.hse.spb.declarations.StatementDeclaration
import ru.hse.spb.declarations.VariableStatement
import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunParser

class StatementVisitor : FunBaseVisitor<StatementDeclaration>() {
    override fun visitFunction(ctx: FunParser.FunctionContext): StatementDeclaration {
        val name = ctx.name.text
        val parameterNames = ctx.parameterNames().IDENTIFIER().map { it.text }
        val body = BlockVisitor().visit(ctx.blockWithBraces())
        return CustomFunctionStatement(name, parameterNames, body)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): StatementDeclaration {
        val name = ctx.name.text
        val initializer = ExpressionVisitor().visit(ctx.initializer)
        return VariableStatement(name, initializer)
    }

    override fun visitWhile_(ctx: FunParser.While_Context): StatementDeclaration {
        val condition = ExpressionVisitor().visit(ctx.condition)
        val body = BlockVisitor().visit(ctx.body)
        return CycleStatement(condition, body)
    }

    override fun visitIf_(ctx: FunParser.If_Context): StatementDeclaration {
        val condition = ExpressionVisitor().visit(ctx.condition)
        val mainBranch = BlockVisitor().visit(ctx.mainBranch)
        val elseBranch = ctx.elseBranch?.let { BlockVisitor().visit(ctx.elseBranch) }
        return IfStatement(condition, mainBranch, elseBranch)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): StatementDeclaration {
        val name = ctx.name.text
        val value = ExpressionVisitor().visit(ctx.value)
        return AssignmentStatement(name, value)
    }

    override fun visitReturn_(ctx: FunParser.Return_Context): StatementDeclaration {
        val expression = ExpressionVisitor().visit(ctx.expression())
        return ReturnStatement(expression)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext): StatementDeclaration {
        return ExpressionVisitor().visit(ctx)
    }
}