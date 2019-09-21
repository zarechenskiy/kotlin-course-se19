grammar FunLanguage;

// PARSER RULES

file
    : block EOF
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
    | assignment
    | returnStatement
    ;

function
    : FUN IDENTIFIER '(' parameterNames ')' blockWithBraces
    ;

parameterNames
    :
    | (IDENTIFIER ',')* IDENTIFIER
    ;

variable
    : VAR IDENTIFIER ('=' expression)?
    ;

whileStatement
    : WHILE '(' expression ')' blockWithBraces
    ;

ifStatement
    : IF '(' expression ')' blockWithBraces (ELSE blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : RETURN expression
    ;

expression
    : simpleExpression
    | expression (DIVISION | MULTIPLICATION | REMINDER) expression
    | expression (ADDITION | SUBSTRACTION)              expression
    | expression (LT | LTE | GT | GTE)                  expression
    | expression (EQ | NEQ)                             expression
    | expression LOGIC_AND                              expression
    | expression LOGIC_OR                               expression
    ;


functionCall
    : IDENTIFIER '(' arguments ')'
    ;

arguments
    :
    | (expression ',')* expression
    ;

simpleExpression
    : functionCall
    | IDENTIFIER
    | LITERAL
    | '(' expression ')'
    ;


// LEXER RULES

FUN
    : 'fun'
    ;

VAR
    : 'var'
    ;

WHILE
    : 'while'
    ;

IF
    : 'if'
    ;

ELSE
    : 'else'
    ;

RETURN
    : 'return'
    ;

MULTIPLICATION
    : '*'
    ;

DIVISION
    : '/'
    ;

ADDITION
    : '+'
    ;

REMINDER
    : '%'
    ;

SUBSTRACTION
    : '-'
    ;

LT
    : '<'
    ;

LTE
    : '<='
    ;

GT
    : '>'
    ;

GTE
    : '>='
    ;

EQ
    : '=='
    ;

NEQ
    : '!='
    ;

LOGIC_AND
    : '&&'
    ;

LOGIC_OR
    : '||'
    ;

IDENTIFIER
    : [_a-zA-Z][_a-zA-Z0-9]*
    ;

WS
    :   [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' (~[\n\r])* -> skip
    ;

LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;