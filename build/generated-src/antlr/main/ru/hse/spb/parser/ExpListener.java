// Generated from Exp.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpParser}.
 */
public interface ExpListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ExpParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ExpParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ExpParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ExpParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#blockWithBraces}.
	 * @param ctx the parse tree
	 */
	void enterBlockWithBraces(ExpParser.BlockWithBracesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#blockWithBraces}.
	 * @param ctx the parse tree
	 */
	void exitBlockWithBraces(ExpParser.BlockWithBracesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ExpParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ExpParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(ExpParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(ExpParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ExpParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ExpParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#parameterNames}.
	 * @param ctx the parse tree
	 */
	void enterParameterNames(ExpParser.ParameterNamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#parameterNames}.
	 * @param ctx the parse tree
	 */
	void exitParameterNames(ExpParser.ParameterNamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(ExpParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(ExpParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(ExpParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(ExpParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#assigment}.
	 * @param ctx the parse tree
	 */
	void enterAssigment(ExpParser.AssigmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#assigment}.
	 * @param ctx the parse tree
	 */
	void exitAssigment(ExpParser.AssigmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(ExpParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(ExpParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(ExpParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(ExpParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(ExpParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(ExpParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#binaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(ExpParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#binaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(ExpParser.BinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#nonBinaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterNonBinaryExpression(ExpParser.NonBinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#nonBinaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitNonBinaryExpression(ExpParser.NonBinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ExpParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ExpParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#operatorFirstLevel}.
	 * @param ctx the parse tree
	 */
	void enterOperatorFirstLevel(ExpParser.OperatorFirstLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#operatorFirstLevel}.
	 * @param ctx the parse tree
	 */
	void exitOperatorFirstLevel(ExpParser.OperatorFirstLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#operatorSecondLevel}.
	 * @param ctx the parse tree
	 */
	void enterOperatorSecondLevel(ExpParser.OperatorSecondLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#operatorSecondLevel}.
	 * @param ctx the parse tree
	 */
	void exitOperatorSecondLevel(ExpParser.OperatorSecondLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#operatorThirdLevel}.
	 * @param ctx the parse tree
	 */
	void enterOperatorThirdLevel(ExpParser.OperatorThirdLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#operatorThirdLevel}.
	 * @param ctx the parse tree
	 */
	void exitOperatorThirdLevel(ExpParser.OperatorThirdLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#operatorFourthLevel}.
	 * @param ctx the parse tree
	 */
	void enterOperatorFourthLevel(ExpParser.OperatorFourthLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#operatorFourthLevel}.
	 * @param ctx the parse tree
	 */
	void exitOperatorFourthLevel(ExpParser.OperatorFourthLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#operatorFifthLevel}.
	 * @param ctx the parse tree
	 */
	void enterOperatorFifthLevel(ExpParser.OperatorFifthLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#operatorFifthLevel}.
	 * @param ctx the parse tree
	 */
	void exitOperatorFifthLevel(ExpParser.OperatorFifthLevelContext ctx);
}