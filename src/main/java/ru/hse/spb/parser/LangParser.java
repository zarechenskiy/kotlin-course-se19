// Generated from Lang.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, VARIABLE=2, FUNCTION=3, WHILE=4, IF=5, ELSE=6, RET=7, IDENTIFIER=8, 
		LITERAL=9, SEPARATOR=10, PLUS=11, MINUS=12, ASTERISK=13, DIVISION=14, 
		REMINDER=15, ASSIGNMENT=16, LPAREN=17, RPAREN=18, LBRACE=19, RBRACE=20, 
		LT=21, GT=22, LE=23, GE=24, EQ=25, NEQ=26, OR=27, AND=28;
	public static final int
		RULE_file = 0, RULE_statement = 1, RULE_block = 2, RULE_block_with_braces = 3, 
		RULE_function = 4, RULE_variable = 5, RULE_parameter_names = 6, RULE_while_expr = 7, 
		RULE_if_expr = 8, RULE_assignment = 9, RULE_return_expr = 10, RULE_expression = 11, 
		RULE_function_call = 12, RULE_arguments = 13, RULE_binary_expression = 14;
	public static final String[] ruleNames = {
		"file", "statement", "block", "block_with_braces", "function", "variable", 
		"parameter_names", "while_expr", "if_expr", "assignment", "return_expr", 
		"expression", "function_call", "arguments", "binary_expression"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'var'", "'fun'", "'while'", "'if'", "'else'", "'ret'", null, 
		null, null, "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'('", "')'", "'{'", 
		"'}'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'||'", "'&&'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "WS", "VARIABLE", "FUNCTION", "WHILE", "IF", "ELSE", "RET", "IDENTIFIER", 
		"LITERAL", "SEPARATOR", "PLUS", "MINUS", "ASTERISK", "DIVISION", "REMINDER", 
		"ASSIGNMENT", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LT", "GT", "LE", 
		"GE", "EQ", "NEQ", "OR", "AND"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Lang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FileContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public While_exprContext while_expr() {
			return getRuleContext(While_exprContext.class,0);
		}
		public If_exprContext if_expr() {
			return getRuleContext(If_exprContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public Return_exprContext return_expr() {
			return getRuleContext(Return_exprContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(39);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(32);
				function();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				variable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(34);
				expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(35);
				while_expr();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(36);
				if_expr();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(37);
				assignment();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(38);
				return_expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << FUNCTION) | (1L << WHILE) | (1L << IF) | (1L << RET) | (1L << IDENTIFIER) | (1L << LITERAL) | (1L << LPAREN))) != 0)) {
				{
				{
				setState(41);
				statement();
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Block_with_bracesContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(LangParser.LBRACE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(LangParser.RBRACE, 0); }
		public Block_with_bracesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block_with_braces; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterBlock_with_braces(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitBlock_with_braces(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitBlock_with_braces(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Block_with_bracesContext block_with_braces() throws RecognitionException {
		Block_with_bracesContext _localctx = new Block_with_bracesContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_block_with_braces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			match(LBRACE);
			setState(48);
			block();
			setState(49);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(LangParser.FUNCTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public TerminalNode LPAREN() { return getToken(LangParser.LPAREN, 0); }
		public Parameter_namesContext parameter_names() {
			return getRuleContext(Parameter_namesContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(LangParser.RPAREN, 0); }
		public Block_with_bracesContext block_with_braces() {
			return getRuleContext(Block_with_bracesContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(FUNCTION);
			setState(52);
			match(IDENTIFIER);
			setState(53);
			match(LPAREN);
			setState(54);
			parameter_names();
			setState(55);
			match(RPAREN);
			setState(56);
			block_with_braces();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VARIABLE() { return getToken(LangParser.VARIABLE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGNMENT() { return getToken(LangParser.ASSIGNMENT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(VARIABLE);
			setState(59);
			match(IDENTIFIER);
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGNMENT) {
				{
				setState(60);
				match(ASSIGNMENT);
				setState(61);
				expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Parameter_namesContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(LangParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(LangParser.IDENTIFIER, i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(LangParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(LangParser.SEPARATOR, i);
		}
		public Parameter_namesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter_names; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterParameter_names(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitParameter_names(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitParameter_names(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Parameter_namesContext parameter_names() throws RecognitionException {
		Parameter_namesContext _localctx = new Parameter_namesContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_parameter_names);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(64);
				match(IDENTIFIER);
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==SEPARATOR) {
					{
					{
					setState(65);
					match(SEPARATOR);
					setState(66);
					match(IDENTIFIER);
					}
					}
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class While_exprContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(LangParser.WHILE, 0); }
		public TerminalNode LPAREN() { return getToken(LangParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(LangParser.RPAREN, 0); }
		public Block_with_bracesContext block_with_braces() {
			return getRuleContext(Block_with_bracesContext.class,0);
		}
		public While_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterWhile_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitWhile_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitWhile_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final While_exprContext while_expr() throws RecognitionException {
		While_exprContext _localctx = new While_exprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_while_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(WHILE);
			setState(75);
			match(LPAREN);
			setState(76);
			expression();
			setState(77);
			match(RPAREN);
			setState(78);
			block_with_braces();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_exprContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(LangParser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(LangParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(LangParser.RPAREN, 0); }
		public List<Block_with_bracesContext> block_with_braces() {
			return getRuleContexts(Block_with_bracesContext.class);
		}
		public Block_with_bracesContext block_with_braces(int i) {
			return getRuleContext(Block_with_bracesContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(LangParser.ELSE, 0); }
		public If_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterIf_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitIf_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitIf_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_exprContext if_expr() throws RecognitionException {
		If_exprContext _localctx = new If_exprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_if_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(IF);
			setState(81);
			match(LPAREN);
			setState(82);
			expression();
			setState(83);
			match(RPAREN);
			setState(84);
			block_with_braces();
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(85);
				match(ELSE);
				setState(86);
				block_with_braces();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGNMENT() { return getToken(LangParser.ASSIGNMENT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(IDENTIFIER);
			setState(90);
			match(ASSIGNMENT);
			setState(91);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Return_exprContext extends ParserRuleContext {
		public TerminalNode RET() { return getToken(LangParser.RET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Return_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_return_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterReturn_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitReturn_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitReturn_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Return_exprContext return_expr() throws RecognitionException {
		Return_exprContext _localctx = new Return_exprContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_return_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(RET);
			setState(94);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public Function_callContext function_call() {
			return getRuleContext(Function_callContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public TerminalNode LITERAL() { return getToken(LangParser.LITERAL, 0); }
		public TerminalNode LPAREN() { return getToken(LangParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(LangParser.RPAREN, 0); }
		public Binary_expressionContext binary_expression() {
			return getRuleContext(Binary_expressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_expression);
		try {
			setState(104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				function_call();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				match(IDENTIFIER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(98);
				match(LITERAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(99);
				match(LPAREN);
				setState(100);
				expression();
				setState(101);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(103);
				binary_expression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_callContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public TerminalNode LPAREN() { return getToken(LangParser.LPAREN, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(LangParser.RPAREN, 0); }
		public Function_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterFunction_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitFunction_call(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitFunction_call(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_callContext function_call() throws RecognitionException {
		Function_callContext _localctx = new Function_callContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_function_call);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(IDENTIFIER);
			setState(107);
			match(LPAREN);
			setState(108);
			arguments();
			setState(109);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SEPARATOR() { return getToken(LangParser.SEPARATOR, 0); }
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_arguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(111);
				expression();
				}
				break;
			case 2:
				{
				setState(112);
				expression();
				{
				setState(113);
				match(SEPARATOR);
				setState(114);
				expression();
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Binary_expressionContext extends ParserRuleContext {
		public Binary_expressionContext left;
		public Token operator;
		public Binary_expressionContext right;
		public TerminalNode LITERAL() { return getToken(LangParser.LITERAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(LangParser.IDENTIFIER, 0); }
		public List<Binary_expressionContext> binary_expression() {
			return getRuleContexts(Binary_expressionContext.class);
		}
		public Binary_expressionContext binary_expression(int i) {
			return getRuleContext(Binary_expressionContext.class,i);
		}
		public TerminalNode ASTERISK() { return getToken(LangParser.ASTERISK, 0); }
		public TerminalNode DIVISION() { return getToken(LangParser.DIVISION, 0); }
		public TerminalNode REMINDER() { return getToken(LangParser.REMINDER, 0); }
		public TerminalNode MINUS() { return getToken(LangParser.MINUS, 0); }
		public TerminalNode PLUS() { return getToken(LangParser.PLUS, 0); }
		public TerminalNode LT() { return getToken(LangParser.LT, 0); }
		public TerminalNode GT() { return getToken(LangParser.GT, 0); }
		public TerminalNode LE() { return getToken(LangParser.LE, 0); }
		public TerminalNode GE() { return getToken(LangParser.GE, 0); }
		public TerminalNode EQ() { return getToken(LangParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(LangParser.NEQ, 0); }
		public TerminalNode AND() { return getToken(LangParser.AND, 0); }
		public TerminalNode OR() { return getToken(LangParser.OR, 0); }
		public Binary_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).enterBinary_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LangListener ) ((LangListener)listener).exitBinary_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LangVisitor ) return ((LangVisitor<? extends T>)visitor).visitBinary_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Binary_expressionContext binary_expression() throws RecognitionException {
		return binary_expression(0);
	}

	private Binary_expressionContext binary_expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Binary_expressionContext _localctx = new Binary_expressionContext(_ctx, _parentState);
		Binary_expressionContext _prevctx = _localctx;
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_binary_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(119);
				match(LITERAL);
				}
				break;
			case IDENTIFIER:
				{
				setState(120);
				match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(143);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(141);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(123);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(124);
						((Binary_expressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASTERISK) | (1L << DIVISION) | (1L << REMINDER))) != 0)) ) {
							((Binary_expressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(125);
						((Binary_expressionContext)_localctx).right = binary_expression(7);
						}
						break;
					case 2:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(126);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(127);
						((Binary_expressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((Binary_expressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(128);
						((Binary_expressionContext)_localctx).right = binary_expression(6);
						}
						break;
					case 3:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(129);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(130);
						((Binary_expressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << GT) | (1L << LE) | (1L << GE))) != 0)) ) {
							((Binary_expressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(131);
						((Binary_expressionContext)_localctx).right = binary_expression(5);
						}
						break;
					case 4:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(132);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(133);
						((Binary_expressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQ || _la==NEQ) ) {
							((Binary_expressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(134);
						((Binary_expressionContext)_localctx).right = binary_expression(4);
						}
						break;
					case 5:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(135);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(136);
						((Binary_expressionContext)_localctx).operator = match(AND);
						setState(137);
						((Binary_expressionContext)_localctx).right = binary_expression(3);
						}
						break;
					case 6:
						{
						_localctx = new Binary_expressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_binary_expression);
						setState(138);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(139);
						((Binary_expressionContext)_localctx).operator = match(OR);
						setState(140);
						((Binary_expressionContext)_localctx).right = binary_expression(2);
						}
						break;
					}
					} 
				}
				setState(145);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 14:
			return binary_expression_sempred((Binary_expressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean binary_expression_sempred(Binary_expressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 2);
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36\u0095\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\5\3*\n\3\3\4\7\4-\n\4\f\4\16\4\60\13\4\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\5\7A\n\7\3\b\3\b\3\b"+
		"\7\bF\n\b\f\b\16\bI\13\b\5\bK\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\5\nZ\n\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\5\rk\n\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\17\3\17\5\17w\n\17\3\20\3\20\3\20\5\20|\n\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7"+
		"\20\u0090\n\20\f\20\16\20\u0093\13\20\3\20\2\3\36\21\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36\2\6\3\2\17\21\3\2\r\16\3\2\27\32\3\2\33\34\2\u009d"+
		"\2 \3\2\2\2\4)\3\2\2\2\6.\3\2\2\2\b\61\3\2\2\2\n\65\3\2\2\2\f<\3\2\2\2"+
		"\16J\3\2\2\2\20L\3\2\2\2\22R\3\2\2\2\24[\3\2\2\2\26_\3\2\2\2\30j\3\2\2"+
		"\2\32l\3\2\2\2\34v\3\2\2\2\36{\3\2\2\2 !\5\6\4\2!\3\3\2\2\2\"*\5\n\6\2"+
		"#*\5\f\7\2$*\5\30\r\2%*\5\20\t\2&*\5\22\n\2\'*\5\24\13\2(*\5\26\f\2)\""+
		"\3\2\2\2)#\3\2\2\2)$\3\2\2\2)%\3\2\2\2)&\3\2\2\2)\'\3\2\2\2)(\3\2\2\2"+
		"*\5\3\2\2\2+-\5\4\3\2,+\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\7\3\2"+
		"\2\2\60.\3\2\2\2\61\62\7\25\2\2\62\63\5\6\4\2\63\64\7\26\2\2\64\t\3\2"+
		"\2\2\65\66\7\5\2\2\66\67\7\n\2\2\678\7\23\2\289\5\16\b\29:\7\24\2\2:;"+
		"\5\b\5\2;\13\3\2\2\2<=\7\4\2\2=@\7\n\2\2>?\7\22\2\2?A\5\30\r\2@>\3\2\2"+
		"\2@A\3\2\2\2A\r\3\2\2\2BG\7\n\2\2CD\7\f\2\2DF\7\n\2\2EC\3\2\2\2FI\3\2"+
		"\2\2GE\3\2\2\2GH\3\2\2\2HK\3\2\2\2IG\3\2\2\2JB\3\2\2\2JK\3\2\2\2K\17\3"+
		"\2\2\2LM\7\6\2\2MN\7\23\2\2NO\5\30\r\2OP\7\24\2\2PQ\5\b\5\2Q\21\3\2\2"+
		"\2RS\7\7\2\2ST\7\23\2\2TU\5\30\r\2UV\7\24\2\2VY\5\b\5\2WX\7\b\2\2XZ\5"+
		"\b\5\2YW\3\2\2\2YZ\3\2\2\2Z\23\3\2\2\2[\\\7\n\2\2\\]\7\22\2\2]^\5\30\r"+
		"\2^\25\3\2\2\2_`\7\t\2\2`a\5\30\r\2a\27\3\2\2\2bk\5\32\16\2ck\7\n\2\2"+
		"dk\7\13\2\2ef\7\23\2\2fg\5\30\r\2gh\7\24\2\2hk\3\2\2\2ik\5\36\20\2jb\3"+
		"\2\2\2jc\3\2\2\2jd\3\2\2\2je\3\2\2\2ji\3\2\2\2k\31\3\2\2\2lm\7\n\2\2m"+
		"n\7\23\2\2no\5\34\17\2op\7\24\2\2p\33\3\2\2\2qw\5\30\r\2rs\5\30\r\2st"+
		"\7\f\2\2tu\5\30\r\2uw\3\2\2\2vq\3\2\2\2vr\3\2\2\2vw\3\2\2\2w\35\3\2\2"+
		"\2xy\b\20\1\2y|\7\13\2\2z|\7\n\2\2{x\3\2\2\2{z\3\2\2\2|\u0091\3\2\2\2"+
		"}~\f\b\2\2~\177\t\2\2\2\177\u0090\5\36\20\t\u0080\u0081\f\7\2\2\u0081"+
		"\u0082\t\3\2\2\u0082\u0090\5\36\20\b\u0083\u0084\f\6\2\2\u0084\u0085\t"+
		"\4\2\2\u0085\u0090\5\36\20\7\u0086\u0087\f\5\2\2\u0087\u0088\t\5\2\2\u0088"+
		"\u0090\5\36\20\6\u0089\u008a\f\4\2\2\u008a\u008b\7\36\2\2\u008b\u0090"+
		"\5\36\20\5\u008c\u008d\f\3\2\2\u008d\u008e\7\35\2\2\u008e\u0090\5\36\20"+
		"\4\u008f}\3\2\2\2\u008f\u0080\3\2\2\2\u008f\u0083\3\2\2\2\u008f\u0086"+
		"\3\2\2\2\u008f\u0089\3\2\2\2\u008f\u008c\3\2\2\2\u0090\u0093\3\2\2\2\u0091"+
		"\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\37\3\2\2\2\u0093\u0091\3\2\2"+
		"\2\r).@GJYjv{\u008f\u0091";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}