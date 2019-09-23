// Generated from Lang.g4 by ANTLR 4.7
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
public class LangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, VARIABLE=2, FUNCTION=3, WHILE=4, IF=5, ELSE=6, RET=7, IDENTIFIER=8, 
		LITERAL=9, SEPARATOR=10, PLUS=11, MINUS=12, ASTERISK=13, DIVISION=14, 
		REMINDER=15, ASSIGNMENT=16, LPAREN=17, RPAREN=18, LBRACE=19, RBRACE=20, 
		LT=21, GT=22, LE=23, GE=24, EQ=25, NEQ=26, OR=27, AND=28;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"WS", "VARIABLE", "FUNCTION", "WHILE", "IF", "ELSE", "RET", "IDENTIFIER", 
		"LITERAL", "SEPARATOR", "PLUS", "MINUS", "ASTERISK", "DIVISION", "REMINDER", 
		"ASSIGNMENT", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LT", "GT", "LE", 
		"GE", "EQ", "NEQ", "OR", "AND"
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


	public LangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Lang.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u009e\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\5\2?\n\2\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\7\t_\n\t\f\t\16\tb\13\t"+
		"\3\n\3\n\3\n\7\ng\n\n\f\n\16\nj\13\n\5\nl\n\n\3\13\3\13\7\13p\n\13\f\13"+
		"\16\13s\13\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35"+
		"\3\35\3\35\2\2\36\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65"+
		"\34\67\359\36\3\2\7\4\2\f\f\17\17\5\2C\\aac|\6\2\62;C\\aac|\3\2\63;\3"+
		"\2\62;\2\u00a2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\3>\3\2\2\2\5B\3\2\2\2\7F\3\2\2\2\tJ\3\2\2\2\13P\3\2\2\2\rS\3"+
		"\2\2\2\17X\3\2\2\2\21\\\3\2\2\2\23k\3\2\2\2\25m\3\2\2\2\27t\3\2\2\2\31"+
		"v\3\2\2\2\33x\3\2\2\2\35z\3\2\2\2\37|\3\2\2\2!~\3\2\2\2#\u0080\3\2\2\2"+
		"%\u0082\3\2\2\2\'\u0084\3\2\2\2)\u0086\3\2\2\2+\u0088\3\2\2\2-\u008a\3"+
		"\2\2\2/\u008c\3\2\2\2\61\u008f\3\2\2\2\63\u0092\3\2\2\2\65\u0095\3\2\2"+
		"\2\67\u0098\3\2\2\29\u009b\3\2\2\2;<\7\17\2\2<?\7\f\2\2=?\t\2\2\2>;\3"+
		"\2\2\2>=\3\2\2\2?@\3\2\2\2@A\b\2\2\2A\4\3\2\2\2BC\7x\2\2CD\7c\2\2DE\7"+
		"t\2\2E\6\3\2\2\2FG\7h\2\2GH\7w\2\2HI\7p\2\2I\b\3\2\2\2JK\7y\2\2KL\7j\2"+
		"\2LM\7k\2\2MN\7n\2\2NO\7g\2\2O\n\3\2\2\2PQ\7k\2\2QR\7h\2\2R\f\3\2\2\2"+
		"ST\7g\2\2TU\7n\2\2UV\7u\2\2VW\7g\2\2W\16\3\2\2\2XY\7t\2\2YZ\7g\2\2Z[\7"+
		"v\2\2[\20\3\2\2\2\\`\t\3\2\2]_\t\4\2\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`"+
		"a\3\2\2\2a\22\3\2\2\2b`\3\2\2\2cl\7\62\2\2dh\t\5\2\2eg\t\6\2\2fe\3\2\2"+
		"\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2il\3\2\2\2jh\3\2\2\2kc\3\2\2\2kd\3\2\2"+
		"\2l\24\3\2\2\2mq\7.\2\2np\7\"\2\2on\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2"+
		"\2\2r\26\3\2\2\2sq\3\2\2\2tu\7-\2\2u\30\3\2\2\2vw\7/\2\2w\32\3\2\2\2x"+
		"y\7,\2\2y\34\3\2\2\2z{\7\61\2\2{\36\3\2\2\2|}\7\'\2\2} \3\2\2\2~\177\7"+
		"?\2\2\177\"\3\2\2\2\u0080\u0081\7*\2\2\u0081$\3\2\2\2\u0082\u0083\7+\2"+
		"\2\u0083&\3\2\2\2\u0084\u0085\7}\2\2\u0085(\3\2\2\2\u0086\u0087\7\177"+
		"\2\2\u0087*\3\2\2\2\u0088\u0089\7>\2\2\u0089,\3\2\2\2\u008a\u008b\7@\2"+
		"\2\u008b.\3\2\2\2\u008c\u008d\7>\2\2\u008d\u008e\7?\2\2\u008e\60\3\2\2"+
		"\2\u008f\u0090\7@\2\2\u0090\u0091\7?\2\2\u0091\62\3\2\2\2\u0092\u0093"+
		"\7?\2\2\u0093\u0094\7?\2\2\u0094\64\3\2\2\2\u0095\u0096\7#\2\2\u0096\u0097"+
		"\7?\2\2\u0097\66\3\2\2\2\u0098\u0099\7~\2\2\u0099\u009a\7~\2\2\u009a8"+
		"\3\2\2\2\u009b\u009c\7(\2\2\u009c\u009d\7(\2\2\u009d:\3\2\2\2\b\2>`hk"+
		"q\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}