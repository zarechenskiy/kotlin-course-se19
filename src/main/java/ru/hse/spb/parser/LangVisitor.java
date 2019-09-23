// Generated from Lang.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(LangParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LangParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(LangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#block_with_braces}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_with_braces(LangParser.Block_with_bracesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(LangParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(LangParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#parameter_names}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_names(LangParser.Parameter_namesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#while_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_expr(LangParser.While_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#if_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_expr(LangParser.If_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_expr(LangParser.Return_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(LangParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(LangParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(LangParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#binary_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_expression(LangParser.Binary_expressionContext ctx);
}