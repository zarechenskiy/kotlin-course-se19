// Generated from Exp.g4 by ANTLR 4.7
package ru.hse.spb.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, IDENTIFIER=13, LITERAL=14, OPERATOR_FIRST_LEVEL=15, 
		OPERATOR_SECOND_LEVEL=16, OPERATOR_THIRD_LEVEL=17, OPERATOR_FOURTH_LEVEL=18, 
		OPERATOR_FIFTH_LEVEL=19, COMMENT=20, WS=21;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "IDENTIFIER", "LITERAL", "OPERATOR_FIRST_LEVEL", 
		"OPERATOR_SECOND_LEVEL", "OPERATOR_THIRD_LEVEL", "OPERATOR_FOURTH_LEVEL", 
		"OPERATOR_FIFTH_LEVEL", "COMMENT", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "'fun'", "'('", "')'", "'var'", "'='", "','", "'while'", 
		"'if'", "'else'", "'return'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "IDENTIFIER", "LITERAL", "OPERATOR_FIRST_LEVEL", "OPERATOR_SECOND_LEVEL", 
		"OPERATOR_THIRD_LEVEL", "OPERATOR_FOURTH_LEVEL", "OPERATOR_FIFTH_LEVEL", 
		"COMMENT", "WS"
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


	public ExpLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Exp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\27\u0098\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\16\3\16\7\16Y\n\16\f\16\16\16\\\13\16\3\17\5\17_\n\17\3\17\3\17"+
		"\5\17c\n\17\3\17\3\17\7\17g\n\17\f\17\16\17j\13\17\5\17l\n\17\3\20\3\20"+
		"\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22w\n\22\3\23\3\23\3\23\3\23\5\23"+
		"}\n\23\3\24\3\24\3\24\3\24\5\24\u0083\n\24\3\25\3\25\3\25\3\25\7\25\u0089"+
		"\n\25\f\25\16\25\u008c\13\25\3\25\5\25\u008f\n\25\3\25\3\25\3\26\3\26"+
		"\5\26\u0095\n\26\3\26\3\26\3\u008a\2\27\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"\3\2\13\5\2C\\aac|\6\2\62;C\\aac|\3\2\62\62\3\2\63;\5\2\'\',,\61\61\4"+
		"\2--//\4\2>>@@\3\3\f\f\5\2\13\f\17\17\"\"\2\u00a2\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\65\3"+
		"\2\2\2\13\67\3\2\2\2\r9\3\2\2\2\17=\3\2\2\2\21?\3\2\2\2\23A\3\2\2\2\25"+
		"G\3\2\2\2\27J\3\2\2\2\31O\3\2\2\2\33V\3\2\2\2\35k\3\2\2\2\37m\3\2\2\2"+
		"!o\3\2\2\2#v\3\2\2\2%|\3\2\2\2\'\u0082\3\2\2\2)\u0084\3\2\2\2+\u0094\3"+
		"\2\2\2-.\7}\2\2.\4\3\2\2\2/\60\7\177\2\2\60\6\3\2\2\2\61\62\7h\2\2\62"+
		"\63\7w\2\2\63\64\7p\2\2\64\b\3\2\2\2\65\66\7*\2\2\66\n\3\2\2\2\678\7+"+
		"\2\28\f\3\2\2\29:\7x\2\2:;\7c\2\2;<\7t\2\2<\16\3\2\2\2=>\7?\2\2>\20\3"+
		"\2\2\2?@\7.\2\2@\22\3\2\2\2AB\7y\2\2BC\7j\2\2CD\7k\2\2DE\7n\2\2EF\7g\2"+
		"\2F\24\3\2\2\2GH\7k\2\2HI\7h\2\2I\26\3\2\2\2JK\7g\2\2KL\7n\2\2LM\7u\2"+
		"\2MN\7g\2\2N\30\3\2\2\2OP\7t\2\2PQ\7g\2\2QR\7v\2\2RS\7w\2\2ST\7t\2\2T"+
		"U\7p\2\2U\32\3\2\2\2VZ\t\2\2\2WY\t\3\2\2XW\3\2\2\2Y\\\3\2\2\2ZX\3\2\2"+
		"\2Z[\3\2\2\2[\34\3\2\2\2\\Z\3\2\2\2]_\7/\2\2^]\3\2\2\2^_\3\2\2\2_`\3\2"+
		"\2\2`l\t\4\2\2ac\7/\2\2ba\3\2\2\2bc\3\2\2\2cd\3\2\2\2dh\t\5\2\2eg\t\5"+
		"\2\2fe\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2il\3\2\2\2jh\3\2\2\2k^\3\2"+
		"\2\2kb\3\2\2\2l\36\3\2\2\2mn\t\6\2\2n \3\2\2\2op\t\7\2\2p\"\3\2\2\2qw"+
		"\t\b\2\2rs\7@\2\2sw\7?\2\2tu\7>\2\2uw\7?\2\2vq\3\2\2\2vr\3\2\2\2vt\3\2"+
		"\2\2w$\3\2\2\2xy\7?\2\2y}\7?\2\2z{\7#\2\2{}\7?\2\2|x\3\2\2\2|z\3\2\2\2"+
		"}&\3\2\2\2~\177\7~\2\2\177\u0083\7~\2\2\u0080\u0081\7(\2\2\u0081\u0083"+
		"\7(\2\2\u0082~\3\2\2\2\u0082\u0080\3\2\2\2\u0083(\3\2\2\2\u0084\u0085"+
		"\7\61\2\2\u0085\u0086\7\61\2\2\u0086\u008a\3\2\2\2\u0087\u0089\13\2\2"+
		"\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u008b\3\2\2\2\u008a\u0088"+
		"\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008d\u008f\t\t\2\2\u008e"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091\b\25\2\2\u0091*\3\2\2\2"+
		"\u0092\u0095\5)\25\2\u0093\u0095\t\n\2\2\u0094\u0092\3\2\2\2\u0094\u0093"+
		"\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0097\b\26\2\2\u0097,\3\2\2\2\16\2"+
		"Z^bhkv|\u0082\u008a\u008e\u0094\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}