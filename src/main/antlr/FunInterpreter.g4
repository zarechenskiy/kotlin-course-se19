grammar FunInterpreter;

file
    : block
    ;

block
    : (NEWLINE* statement (NEWLINE+ statement)*)?
    ;

blockWithBraces
    : '{' NEWLINE* block NEWLINE* '}'
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
    : 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

parameterNames
    :
    | (parameters+=IDENTIFIER ',')* parameters+=IDENTIFIER
    ;

whileStatement
    : 'while' '(' whileExpr=expression ')' blockWithBraces
    ;

ifStatement
    :'if' '(' ifExpr=expression ')' trueBlock=blockWithBraces ('else' falseBlock=blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : 'return' expression
    ;

functionCall
    : IDENTIFIER '(' arguments ')'
    ;

arguments
    :
    |  (args+=expression ',' )* args+=expression
    ;

expression
    : logicExpr
    ;

logicExpr
    : compareExpr (LOGIC_OPS logicExpr)*
    ;

compareExpr
    : expr (COMP_OPS expr)?
    ;

expr
    : term (PLUS_MINUS expr)*
    ;

term
    : factor (MULT_DIVIDE term)*
    ;

factor
    : IDENTIFIER
    | literal
    | '(' expression ')'
    | functionCall
    ;

IDENTIFIER
    : [_a-zA-Z][_a-zA-Z0-9]*
    ;

literal
    : LITERAL
    ;

PLUS_MINUS
    : ('+'|'-')
    ;

MULT_DIVIDE
    : ('*'|'/'|'%')
    ;

COMP_OPS
    : ('>'|'<'|'>='|'<='|'=='|'!=')
    ;

LOGIC_OPS
    : ('||' | '&&')
    ;

LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

WS : [ \t] -> skip;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip;