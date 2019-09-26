grammar FunInterpreter;

// foo(PI)
// foo(1, 2, PI)

file
    : block
    ;

block
    : (NEWLINE* statements+=statement NEWLINE*)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variableAssign
    | whileStatement
    | ifStatement
    | assignment
    | returnStatement
    | expression
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' thenBlock ('else' elseBlock)?
    ;

thenBlock
    : blockWithBraces
    ;

elseBlock
    : blockWithBraces
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : 'return' expression
    ;

function
    : 'fun'  IDENTIFIER '(' functionArgumentNames ')' blockWithBraces
    ;

functionArgumentNames
    : (parameters += IDENTIFIER)(',' parameters += IDENTIFIER)*
    |
    ;

funcitonArguments
    : (arguments += expression)(',' arguments += expression)*
    |
    ;

variableAssign
    : 'var' IDENTIFIER  ( '='  expression )?
    ;

number
    : NUMBER_LITERAL
    ;

expression
    : binaryExpression
    ;

variable
    : IDENTIFIER
    ;

functionCall
    : IDENTIFIER '(' funcitonArguments ')'
    ;

binaryExpression
    : logicalExpression
    ;

logicalExpression
    : compExpression (OP_LOGICAL compExpression)*
    ;

compExpression
    : addExpression (OP_COMP addExpression)*
    ;

addExpression
    : multExpression (OP_ADD multExpression)*
    ;

multExpression
    : justExpression (OP_MULT justExpression)*
    ;

justExpression
    : functionCall
    | number
    | variable
    | bracketsExpression
    ;

bracketsExpression
    : '(' expression ')'
    ;

// ------------- RULES FOR LEXER -------------
OP_COMP
    : '<'
    | '<='
    | '=='
    | '!='
    | '>'
    | '>='
    ;

OP_LOGICAL
    : '||'
    | '&&'
    ;

OP_ADD
    : [-+]
    ;

OP_MULT
    : [*/%]
    ;


IDENTIFIER
    : ([a-zA-Z])+
    ;

NUMBER_LITERAL
    : '0'
    | ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES : (' ' | '\t' | '\n') -> skip;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;