grammar FPL;

file
    : block EOF
    ;

block
    : statements+=statement (statements+=statement)*
    ;

blockWithBraces
    : LBRA block RBRA
    ;

statement
    : exprStmt
    | retStmt
    | funStmt
    | varStmt
    | whileStmt
    | ifStmt
    | assignStmt
    ;

varStmt
    : VARKW IDENTIFIER (ASSGNKW exprStmt)?
    ;

whileStmt
    : WHLKW LPAR cond=exprStmt RPAR body=blockWithBraces
    ;

ifStmt
    : IFKW LPAR cond=exprStmt RPAR thenClause=blockWithBraces (ELSKW elseClause=blockWithBraces)?
    ;

assignStmt
    : IDENTIFIER ASSGNKW exprStmt
    ;

funStmt
    : FUNKW IDENTIFIER LPAR (parameters+=parameter (SEP parameters+=parameter)*)? RPAR blockWithBraces
    ;
retStmt
    : RETKW exprStmt
    ;

exprStmt
    : left=exprStmt op=(MULT | DIV) right=exprStmt
    | left=exprStmt op=(EQ | GT | GE | LT | LE | NEQ) right=exprStmt
    | left=exprStmt op=(PLUS | MINUS) right=exprStmt
    | <assoc=right> left=exprStmt op=CONJ right=exprStmt
    | <assoc=right> left=exprStmt op=DISJ right=exprStmt
    | funCall
    | LPAR exprInParen=exprStmt RPAR
    | LITERAL
    | IDENTIFIER
    ;

funCall
    : IDENTIFIER LPAR (arguments+=argument (SEP arguments+=argument)*)? RPAR
    ;

argument
    : exprStmt
    ;

parameter
    : IDENTIFIER
    ;

//////// LEXER

NEQ
    : '!='
    ;

LE
    : '<='
    ;

LT
    : '<'
    ;

GE
    : '>='
    ;

GT
    : '>'
    ;

EQ
    : '=='
    ;

DIV
    : '/'
    ;

MULT
    : '*'
    ;

MINUS
    : '-'
    ;

PLUS
    : '+'
    ;

DISJ
    : '||'
    ;

CONJ
    : '&&'
    ;

SEP
    : ','
    ;

LBRA
    : '{'
    ;

RBRA
    : '}'
    ;

LPAR
    : '('
    ;

RPAR
    : ')'
    ;

VARKW
    : 'var'
    ;

FUNKW
    : 'fun'
    ;

RETKW
    : 'return'
    ;

WHLKW
    : 'while'
    ;

IFKW
    : 'if'
    ;

ELSKW
    : 'else'
    ;

ASSGNKW
    : '='
    ;

IDENTIFIER
	: '_'* ([a-zA-Z]) ('_' | [0-9] |  [a-zA-Z])*
	;

LITERAL
    : '-'? ([1-9]) ([0-9])*
    | '-'? '0'
    ;

WS
    : [ \t\n\r]+ -> skip
    ;

NEWLINE
	: [\n]+
	;

LINE_COMMENT
    : '//' (~('\n'))* (NEWLINE | EOF) -> skip
    ;
