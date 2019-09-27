grammar FunCall;

file
    : block
    ;

block
    : (NEWLINE* SPACES* statement SPACES* NEWLINE*)*
    ;

brBlock
    : SPACES* '{' NEWLINE block NEWLINE SPACES* '}'
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
    : 'fun' SPACES IDENTIFIER SPACES* '(' params ')' brBlock
    ;

var
    : 'var' SPACES IDENTIFIER SPACES* ('=' SPACES* expr)?
    ;

params
    : IDENTIFIER (',' SPACES* IDENTIFIER)*
    |
    ;

whileSt
    : 'while' SPACES* '(' expr ')' brBlock
    ;

ifSt
    : 'if' SPACES* '(' expr ')' brBlock SPACES* ('else' brBlock)?
    ;

assign
    : IDENTIFIER SPACES* '=' SPACES* expr
    ;

returnSt
    : 'return' SPACES* expr
    ;

expr
    : 'println' '(' args ')'                                           #printExpr
    | IDENTIFIER '(' args ')'                                          #callExpr
    | op=(MINUS | EXC) expr                                            #unaryExpr
    | expr SPACES* op=(MUL | DIV | MOD)                SPACES* expr    #prodExpr
    | expr SPACES* op=(PLUS | MINUS)                   SPACES* expr    #sumExpr
    | expr SPACES* op=(EQ | NEQ | GE | LE | GEQ | LEQ) SPACES* expr    #ordExpr
    | expr SPACES* op=AND                              SPACES* expr    #andExpr
    | expr SPACES* op=OR                               SPACES* expr    #orExpr
    | IDENTIFIER                                                       #idExpr
    | LITERAL                                                          #litExpr
    | '(' expr ')'                                                     #brExpr
    ;

args
    : expr (',' SPACES* expr)*
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