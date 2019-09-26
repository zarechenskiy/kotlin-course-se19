grammar FunCall;

file
    : block
    ;

block
    : (NEWLINE* SPACES* statement+ SPACES* NEWLINE*)*
    ;

brBlock
    : '{' block '}' NEWLINE*
    ;

statement
    : func
    | var
    | expr
    | whileSt
    | ifSt
    | assign
    | returnSt
    ;

func
    : 'fun' SPACES IDENTIFIER '(' params ')' SPACES brBlock
    ;

var
    : 'var' SPACES IDENTIFIER SPACES ('=' SPACES expr)?
    ;

params
    : IDENTIFIER (',' IDENTIFIER)*
    |
    ;

whileSt
    : 'while' SPACES '(' expr ')' SPACES brBlock
    ;

ifSt
    : 'if' SPACES '(' expr ')' SPACES brBlock SPACES ('else' SPACES brBlock)?
    ;

assign
    : IDENTIFIER SPACES '=' SPACES expr
    ;

returnSt
    : 'return' SPACES expr NEWLINE
    ;

expr
    : 'println' '(' args ')'                                     #printExpr
    | IDENTIFIER '(' args ')'                                    #callExpr
    | op=(MINUS | EXC) expr                                      #unaryExpr
    | expr           op=(MUL | DIV | MOD)                expr    #prodExpr
    | expr           op=(PLUS | MINUS)                   expr    #sumExpr
    | expr           op=(EQ | NEQ | GE | LE | GEQ | LEQ) expr    #ordExpr
    | expr           op=AND                              expr    #andExpr
    | expr           op=OR                               expr    #orExpr
    | IDENTIFIER                                                 #idExpr
    | LITERAL                                                    #litExpr
    | '(' expr ')'                                               #brExpr
    ;

args
    : expr (',' SPACES expr)*
    |
    ;

// ------------- RULES FOR LEXER -------------

PLUS  : '+';
MINUS : '-';
MUL   : '*';
DIV   : '/';
MOD   : '%';
GE    : '>';
LE    : '<';
GEQ   : '>=';
LEQ   : '<=';
EQ    : '==';
NEQ   : '!=';
OR    : '||';
AND   : '&&';
EXC   : '!';

IDENTIFIER
    : ([a-zA-Z])+
    ;

LITERAL
    : '0'
    | ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES
    : ' '+
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;