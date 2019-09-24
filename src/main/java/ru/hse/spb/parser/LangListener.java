// Generated from Lang.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LangParser}.
 */
public interface LangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(LangParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(LangParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LangParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LangParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(LangParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(LangParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#block_with_braces}.
	 * @param ctx the parse tree
	 */
	void enterBlock_with_braces(LangParser.Block_with_bracesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#block_with_braces}.
	 * @param ctx the parse tree
	 */
	void exitBlock_with_braces(LangParser.Block_with_bracesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(LangParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(LangParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(LangParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(LangParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#parameter_names}.
	 * @param ctx the parse tree
	 */
	void enterParameter_names(LangParser.Parameter_namesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#parameter_names}.
	 * @param ctx the parse tree
	 */
	void exitParameter_names(LangParser.Parameter_namesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#while_expr}.
	 * @param ctx the parse tree
	 */
	void enterWhile_expr(LangParser.While_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#while_expr}.
	 * @param ctx the parse tree
	 */
	void exitWhile_expr(LangParser.While_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#if_expr}.
	 * @param ctx the parse tree
	 */
	void enterIf_expr(LangParser.If_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#if_expr}.
	 * @param ctx the parse tree
	 */
	void exitIf_expr(LangParser.If_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#return_expr}.
	 * @param ctx the parse tree
	 */
	void enterReturn_expr(LangParser.Return_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#return_expr}.
	 * @param ctx the parse tree
	 */
	void exitReturn_expr(LangParser.Return_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(LangParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(LangParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#function_call}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(LangParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#function_call}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(LangParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(LangParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(LangParser.ArgumentsContext ctx);
}