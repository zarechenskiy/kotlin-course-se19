grammar FunCall;

file
    : block
    ;

block
    : (statement)*
    ;

blockWithBraces
    : '{' block '}'
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

expression
    : functionCall
    | binaryExperession
    | innerExpression
    ;

binaryExperession : left=innerExpression binaryOperator right=innerExpression;

value
    : IDENTIFIER
    | LITERAL;


innerExpression
    : '(' expression ')'
    | functionCall
    | value;

whileOp
    : 'while' '(' expression ')' blockWithBraces;

ifOp
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

returnOp : 'return' expression;

assigment : IDENTIFIER '=' expression;

function : 'fun' IDENTIFIER '(' argumentList ')' blockWithBraces;

parameterList
    : (expression)?
    | expression ',' parameterList
    ;

functionCall : IDENTIFIER '(' parameterList ')';

argumentList
    : (IDENTIFIER)?
    | IDENTIFIER ',' argumentList
    ;

statement
    : functionCall
    | variable
    | function
    | expression
    | whileOp
    | ifOp
    | assigment
    | returnOp
    ;

binaryOperator
    : '+'
    | '-'
    | '/'
    | '%'
    | '*'
    | '&&'
    | '||'
    | '>'
    | '<'
    | '=='
    | '!='
    | '>='
    | '<='
    ;

// ------------- RULES FOR LEXER -------------


LITERAL
    : '0'
    | '-'? [1-9]([0-9])*
    ;

IDENTIFIER
    : ([_a-zA-Z])+([_0-9a-zA-Z])*
    ;

WS
    : (NEWLINE
    | SPACES) -> skip;

NEWLINE
    : ([\r\n])
    ;

SPACES
    : [ \t] -> skip ;

COMMENT:
    '//' [^\r\n]* -> skip;
