grammar FunLang;

file
    : block
    ;

block
    : (statement)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expr
    | whileSt
    | ifSt
    | assign
    | returnSt
    ;

function
    : FUN IDENTIFIER '(' parameterNames ')' blockWithBraces
    ;



parameterNames
     :
     | (parameters += IDENTIFIER ',')* (parameters += IDENTIFIER)
     ;

variable
    : VAR IDENTIFIER ('=' expr)?
    ;

whileSt
    : WHILE '(' expr ')' blockWithBraces
    ;

ifSt
    : IF '(' expr ')' blockWithBraces (ELSE blockWithBraces)?
    ;

assign
    : IDENTIFIER '=' expr
    ;

returnSt
    : RETURN expr
    ;

expr
    : expr (MUL | DIV | MOD) expr
    | expr (ADD | SUB) expr
    | expr (GT | LS | GE | LE) expr
    | expr (EQ | NE) expr
    | expr (AND) expr
    | expr (OR) expr
    | functionCall
    | IDENTIFIER
    | LITERAL
    | '(' expr ')'
    ;

functionCall
    : IDENTIFIER '(' args ')'
    ;

args
    :
    | (expr ',')* expr
    ;

FUN: 'fun';
VAR: 'var';
WHILE: 'while';
IF: 'if';
ELSE: 'else';
RETURN: 'return';
MUL: '*';
DIV: '/';
MOD: '%';
ADD: '+';
SUB: '-';
GT: '>';
LS: '<';
GE: '>=';
LE: '<=';
EQ: '==';
NE: '!=';
AND: '&&';
OR: '||';

IDENTIFIER: ([a-zA-Z])+;

LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;
WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' (~[\n\r])* -> skip
    ;