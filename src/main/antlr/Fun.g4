grammar Fun;

file
    : block
    ;

block
    : (NEWLINE* statement NEWLINE*)*
    ;

blockWithBraces
    : NEWLINE* '{' block '}' NEWLINE*
    ;

statement
    : function
    | variable
    | expression
    | whileRule
    | ifRule
    | assignment
    | returnRule
    ;

function
    : 'fun' IDENTIFIER '(' (parameters += IDENTIFIER ',' | parameters += IDENTIFIER ')')* blockWithBraces
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

whileRule
    : 'while' '(' expression ')' blockWithBraces
    ;

ifRule
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnRule
    : 'return' expression
    ;

expression
    : functionCall #fCall
    | MINUS expression #unaryMinus
    | expression operator=(MULT | DIV | MOD) expression #mulExpr
    | expression operator=(PLUS | MINUS) expression #addExpr
    | expression operator=(LTEQ | GTEQ | LT | GT) expression #compExpr
    | expression operator=(EQ | NEQ) expression #eqExpr
    | expression AND expression # andExpr
    | expression OR expression #orExpr
    | IDENTIFIER # identExpr
    | NUMBER_LITERAL # numExpr
    | '(' expression ')' # bracketExpr
    ;

functionCall
    : IDENTIFIER '(' (arguments+=expression ',' | arguments += expression ')')*
    ;

// ------------- RULES FOR LEXER -------------

IDENTIFIER
    : [_a-zA-Z][_a-zA-Z0-9]*
    ;

NUMBER_LITERAL
    : ([1-9])([0-9])*
    | ([0])
    ;

NEWLINE
    : [\r\n]+
    ;

PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
MOD : '%';
GT : '>';
LT : '<';
GTEQ : '>=';
LTEQ : '<=';
EQ : '==';
NEQ : '!=';
OR : '||';
AND : '&&';

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

TAB
    : [ \t] -> skip
    ;

SPACE
    : ' ' -> skip
    ;