// Generated from Exp.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ExpParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(ExpParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#blockWithBraces}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockWithBraces(ExpParser.BlockWithBracesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(ExpParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(ExpParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(ExpParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#parameterNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterNames(ExpParser.ParameterNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(ExpParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(ExpParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#assigment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssigment(ExpParser.AssigmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(ExpParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(ExpParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(ExpParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#binaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpression(ExpParser.BinaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#nonBinaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonBinaryExpression(ExpParser.NonBinaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(ExpParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#operatorFirstLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorFirstLevel(ExpParser.OperatorFirstLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#operatorSecondLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorSecondLevel(ExpParser.OperatorSecondLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#operatorThirdLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorThirdLevel(ExpParser.OperatorThirdLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#operatorFourthLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorFourthLevel(ExpParser.OperatorFourthLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#operatorFifthLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorFifthLevel(ExpParser.OperatorFifthLevelContext ctx);
}