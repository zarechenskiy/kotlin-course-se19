package ru.hse.spb

import ru.hse.spb.parser.FPLBaseVisitor
import ru.hse.spb.parser.FPLParser
import java.util.*

class FplParseTreeVisitor : FPLBaseVisitor<Any>() {

    override fun visitFile(ctx: FPLParser.FileContext): Any {
        return ctx.block().accept(this)
    }

    override fun visitBlockWithBraces(ctx: FPLParser.BlockWithBracesContext): Any {
        return ctx.block().accept(this)
    }

    // Just need to construct list of statements.
    override fun visitBlock(ctx: FPLParser.BlockContext): FplTree {
        val list = ctx.statements.map {
            it.accept(this) as Statement
        }
        return FplTree(list)
    }

    /**
     * Variable statement:
     * 'var' IDENTIFIER ('=' exprStmt)?
     */
    override fun visitVarStmt(ctx: FPLParser.VarStmtContext): Statement {
        val expr = ctx.exprStmt()?.let { Optional.of(it.accept(this) as Expr) } ?: Optional.empty()
        return Variable(ctx.IDENTIFIER().toString(), expr)
    }

    /**
     * Possible forms:
     * - expr BINOP expr
     * - Function Call
     * - Literal
     * - Identifier
     * - Expr in parenthesis
     */
    override fun visitExprStmt(ctx: FPLParser.ExprStmtContext): Expr = with(ctx) {
        LITERAL()?.let { Literal(it.text.toInt()) } ?: IDENTIFIER()?.let { Identifier(it.text) }
        ?: funCall()?.let { fcall ->
            val args: List<Expr> = fcall.arguments.map { it.accept(this@FplParseTreeVisitor) as Expr }
            val identifier = fcall.IDENTIFIER().toString()
            FunCall(identifier, args)
        } ?: if (exprInParen != null) {
            exprInParen.accept(this@FplParseTreeVisitor) as Expr
        } else {
            // So this is a binary expression.
            val left = this.left.accept(this@FplParseTreeVisitor) as Expr
            val right = this.right.accept(this@FplParseTreeVisitor) as Expr
            val op = BinaryOperation.operatorToName(this.op.text)
            Binary(left, right, op)
        }
    }

    /**
     * Assignment statement:
     *  IDENTIDIER '=' exprStmt
     */
    override fun visitAssignStmt(ctx: FPLParser.AssignStmtContext): Statement {
        val identifier = ctx.IDENTIFIER().toString()
        val expr = ctx.exprStmt().accept(this) as Expr
        return Assign(identifier, expr)
    }

    /**
     * While statement:
     *   'while' '(' cond ')' '{' body '}'
     */
    override fun visitWhileStmt(ctx: FPLParser.WhileStmtContext): Statement {
        val condition = ctx.cond.accept(this) as Expr
        val body = ctx.body.accept(this) as FplTree

        return While(condition, body)
    }

    /**
     * If statement:
     *   'If' '(' expr ')' '{' thenClause '}' ('else' '{' elseClause '}')?
     */
    override fun visitIfStmt(ctx: FPLParser.IfStmtContext): Any {
        val condition = ctx.cond.accept(this) as Expr
        val thenClause = ctx.thenClause.accept(this) as FplTree
        val elseClause = ctx.elseClause?.accept(this)?.let { Optional.of(it as FplTree) } ?: Optional.empty()
        return If(condition, thenClause, elseClause)
    }

    /**
     * Function definition statement:
     *   'fun' IDENTIFIER '(' parameters ')' '{' body '}'
     */
    override fun visitFunStmt(ctx: FPLParser.FunStmtContext): Statement {
        val identifier = ctx.IDENTIFIER().toString()
        val parameters = ctx.parameters.map { it.text }
        val body = ctx.blockWithBraces().accept(this) as FplTree

        return Fun(identifier, parameters, body)
    }

    /**
     * Return statement:
     *   'return' expr
     */
    override fun visitRetStmt(ctx: FPLParser.RetStmtContext): Statement {
        val expr = ctx.exprStmt().accept(this) as Expr
        return Return(expr)
    }

    /**
     * FunCall expression:
     *   IDENTIFIER '(' arguments ')'
     */
    override fun visitFunCall(ctx: FPLParser.FunCallContext): Statement {
        val identifier = ctx.IDENTIFIER().toString()
        val arguments = ctx.arguments.map { it.accept(this) as Expr }
        return FunCall(identifier, arguments)
    }

    /**
     * Argument is just an expression.
     */
    override fun visitArgument(ctx: FPLParser.ArgumentContext): Expr = ctx.exprStmt().accept(this) as Expr
}