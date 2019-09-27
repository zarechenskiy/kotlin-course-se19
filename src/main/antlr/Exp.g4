grammar Exp;

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
    | expression
    | whileStatement
    | ifStatement
    | assigment
    | returnStatement
    ;

function
    : 'fun' identifier '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' identifier ('=' expression)?
    ;

parameterNames
    :
    | (identifier + ',')* identifier
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;


assigment
   : identifier '=' expression
   ;


returnStatement
    : 'return' expression
    ;


expression
    : binaryExpression
    ;

functionCall
    : identifier '(' arguments ')'
    ;


arguments
    :
    | (expression ',')* expression
    ;

binaryExpression
    : nonBinaryExpression
    | binaryExpression operatorFirstLevel binaryExpression
    | binaryExpression operatorSecondLevel binaryExpression
    | binaryExpression operatorThirdLevel binaryExpression
    | binaryExpression operatorFourthLevel binaryExpression
    | binaryExpression operatorFifthLevel binaryExpression
    ;

nonBinaryExpression
    : functionCall
    | identifier
    | literal
    | '(' expression ')'
    ;

identifier:
    IDENTIFIER
    ;

literal
    : LITERAL
    ;

operatorFirstLevel
    : OPERATOR_FIRST_LEVEL
    ;

operatorSecondLevel
    : OPERATOR_SECOND_LEVEL
    ;

operatorThirdLevel
: OPERATOR_THIRD_LEVEL
;

operatorFourthLevel
: OPERATOR_FOURTH_LEVEL
;

operatorFifthLevel
: OPERATOR_FIFTH_LEVEL
;

IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

LITERAL
    : '-'? [0]
    | '-'? [1-9][0-9]*
    ;

OPERATOR_FIRST_LEVEL
    : '/'
    | '%'
    | '*'
    ;

OPERATOR_SECOND_LEVEL
    : '+'
    | '-'
    ;

OPERATOR_THIRD_LEVEL
    : '>'
    | '<'
    | '>='
    | '<='
    ;

OPERATOR_FOURTH_LEVEL
    : '=='
    | '!='
    ;

OPERATOR_FIFTH_LEVEL
    : '||'
    | '&&'
    ;

COMMENT :
    '//'.*?('\n' | EOF)
-> skip;

WS : ( COMMENT | ' ' | '\t' | '\r' | '\n' ) -> skip;