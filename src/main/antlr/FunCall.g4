grammar FunCall;

file
    : block
    ;

block
    : (NEWLINE* statements+=statement NEWLINE*)*
    ;

block_with_braces
    : '{' SPACES? block  SPACES? '}'
    ;

statement
    : function
    | variable
    | expression
    | while
    | if
    | assignment
    | return
    ;

function
    : 'fun' IDENTIFIER SPACES? '(' parameterNames? ')' SPACES? block_with_braces
    ;

variable
    : 'var' SPACES IDENTIFIER SPACES? ('=' SPACES? expression)?
    ;

parameterNames
    : IDENTIFIER{','}
    ;

while
    : 'while' SPACES? '(' expression ')' SPACES? block_with_braces
    ;

if
    : 'if' SPACES? '(' expression ')' SPACES? block_with_braces ('else' block_with_braces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

expression
    : functionCall
    | '(' SPACES? expression SPACES? ')'
    // binariExpression
    | expression SPACES? ('*' | '/' | '%') SPACES? expression
    | expression SPACES? ('+' | '-') SPACES? expression
    | expression SPACES? ('<' | '>') '='? SPACES? expression
    | expression SPACES? ('!' | '=') '=' SPACES? expression
    | expression SPACES? ('&&') SPACES? expression
    | expression SPACES? ('||') SPACES? expression
    //
    | IDENTIFIER
    | NUMBER_LITERAL
    ;

functionCall
    : IDENTIFIER '(' arguments? ')'
    ;

arguments
    : expression{','}
    ;

return
    : 'return' SPACES expression
    ;

/*
    Арифметическое выражение с операциями: +, -, *, /, %, >, <, >=, <=, ==, !=, ||, &&
    Семантика и приоритеты операций примерно как в Си
*/
//binariExpression
//    : binariExpressionPrecedence3
//    | binariExpressionPrecedence4
//    | binariExpressionPrecedence6
//    | binariExpressionPrecedence7
//    | binariExpressionPrecedence11
//    | binariExpressionPrecedence12
//    ;
//
//// https://en.cppreference.com/w/c/language/operator_precedence
//binariExpressionPrecedence3
//    : expression SPACES* ('*' | '/' | '%') SPACES* expression
//    ;
//
//binariExpressionPrecedence4
//    : expression SPACES* ('+' | '-') SPACES* expression
//    ;
//
//binariExpressionPrecedence6
//    : expression SPACES* ('<' | '>') '='? SPACES* expression
//    ;
//
//binariExpressionPrecedence7
//    : expression SPACES* ('!' | '=') '=' SPACES* expression
//    ;
//
//binariExpressionPrecedence11
//    : expression SPACES* ('&&') SPACES* expression
//    ;
//binariExpressionPrecedence12
//    : expression SPACES* ('||') SPACES* expression
//    ;
// ------------- RULES FOR LEXER -------------

IDENTIFIER
    : ([a-zA-Z])+
    ;

NUMBER_LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES
    : ' '+
    ;
