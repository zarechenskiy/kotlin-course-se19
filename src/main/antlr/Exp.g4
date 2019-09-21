grammar Exp;

file
 : block
 ;

block
 : statement*
 ;

brBlock
 : '{' block '}'
 ;

statement
 : func
 | var
 | expr
 | whl
 | cond
 | asgn
 | ret
 | END_LINE
 ;

func
 : 'fun' IDENTIFIER '(' params ')' brBlock
 ;

var
 : 'var' IDENTIFIER ('=' expr)? END_LINE
 ;

params
 : IDENTIFIER (',' IDENTIFIER)*
 | /* epsilon */
 ;

whl
 : 'while' '(' expr ')' brBlock
 ;

cond
 : 'if' '(' expr ')' brBlock ('else' brBlock)?
 ;

asgn
 : IDENTIFIER '=' expr END_LINE
 ;

ret
 : 'return' expr
 ;

expr
 : call                                  #callExpr
 | MINUS expr                            #unaryExpr
 | expr op=(MULT | DIV | MOD) expr       #multExpr
 | expr op=(PLUS | MINUS) expr           #addExpr
 | expr op=(LTEQ | GTEQ | LT | GT) expr  #relExpr
 | expr op=(EQ | NEQ) expr               #eqExpr
 | expr AND expr                         #andExpr
 | expr OR expr                          #orExpr
 | IDENTIFIER                            #idExpr
 | (TRUE | FALSE)                        #boolExpr
 | LITERAL                               #litExpr
 | '(' expr ')'                          #parExpr
 ;

call
 : IDENTIFIER '(' args ')'
 ;

args
 : expr (',' expr)*
 | /* epsilon */
 ;

TRUE : 'true';
FALSE : 'false';

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

IDENTIFIER
 : [_a-zA-Z] [_a-zA-Z0-9]*
 ;

LITERAL
 : [0-9]
 | [1-9] [0-9]+
 ;

END_LINE
 : [\r\n]+
 ;

COMMENT
 : '//' ~[\r\n]* -> skip
 ;

SPACE
 : [ \t] -> skip
 ;
