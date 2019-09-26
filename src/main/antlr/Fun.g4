grammar Fun;

@parser::members
{
  public java.util.HashMap<String, Double> memory = new java.util.HashMap<String, Double>();

  @Override
  public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException ex)
  {
    throw new RuntimeException(msg);
  }
}

@lexer::members
{
  @Override
  public void recover(RecognitionException ex)
  {
    throw new RuntimeException(ex.getMessage());
  }
}

file
    : block
    ;

block
    : ((NEWLINE|SPACES)* statements+=statement)* (NEWLINE|SPACES)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileStatement
    | ifStatement
    | assignment
    | returnStatement
    ;

function
    : 'fun' SPACES identifier SPACES* '(' SPACES* parameterNames SPACES* ')' SPACES* blockWithBraces
    ;

variable
    : 'var' SPACES identifier (SPACES* '=' SPACES* expression)?
    ;

parameterNames
    : (argumentsList+=identifier (SPACES* ',' SPACES* argumentsList+=identifier)*)?
    ;

whileStatement
    : 'while' SPACES* '(' SPACES* expression SPACES* ')' SPACES* blockWithBraces
    ;

ifStatement
    : 'if' SPACES* '(' SPACES* expression SPACES* ')' SPACES* blockWithBraces (NEWLINE* SPACES* elseBlock)?
    ;

elseBlock
    : 'else' SPACES* blockWithBraces
    ;

assignment
    : identifier SPACES* '=' SPACES* expression
    ;

returnStatement
    : 'return' SPACES expression
    ;

expression
    : binaryExpression
    ;

nonBinaryExpression
    : functionCall
    | identifier
    | literal
    | '(' SPACES* expression SPACES* ')'
    ;

functionCall
    : identifier SPACES* '(' SPACES* arguments SPACES* ')'
    ;

arguments
    : (argumentsList+=expression (SPACES* ',' SPACES* argumentsList+=expression)*)?
    ;

/*binaryExpression
    : logicalOr
    | logicalAnd
    | equal, notEqual
    | lessEqual, greaterEqual, less, greater
    | addition, subtraction
    | multiplication, division, remainder
    ;*/

binaryExpression
    : lessOrBinaryExpression
    | binaryExpression SPACES* operation='||' SPACES* lessOrBinaryExpression
    ;

lessOrBinaryExpression
    : lessAndBinaryExpression
    | lessOrBinaryExpression SPACES operation='&&' SPACES lessAndBinaryExpression
    ;

lessAndBinaryExpression
    : lessEqualityBinaryExpression
    | lessAndBinaryExpression SPACES operation=('=='|'!=') SPACES lessEqualityBinaryExpression
    ;

lessEqualityBinaryExpression
    : lessCompareBinaryExpression
    | lessEqualityBinaryExpression SPACES operation=('<'|'>'|'>='|'<=') SPACES lessCompareBinaryExpression
    ;

lessCompareBinaryExpression
    : lessSubAddBinaryExpression
    | lessCompareBinaryExpression SPACES operation=('+'|'-') SPACES lessSubAddBinaryExpression
    ;

lessSubAddBinaryExpression
    : nonBinaryExpression
    | lessSubAddBinaryExpression SPACES operation=('*'|'/'|'%') SPACES nonBinaryExpression
    ;

identifier
    : IDENTIFIER
    | unaryMinus
    ;

unaryMinus
    : '-' IDENTIFIER
    ;

literal
    : LITERAL
    ;


// ------------- RULES FOR LEXER -------------

IDENTIFIER
    : ([a-zA-Z_])([a-zA-Z_0-9])*
    ;

LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES
    : (' '|'\t')+
    ;

COMMENT
    : '//'~[\n\r]* -> channel(HIDDEN)
    ;