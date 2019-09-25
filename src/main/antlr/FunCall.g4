grammar FunCall;

// foo(PI)
// foo(1, 2, PI)

file
    : block
    ;

block
    : (NEWLINE* statements+=statement NEWLINE*)*
    ;

statement
    : functionCall
    ;

functionCall
    : IDENTIFIER '(' (arguments+=argument ',' SPACES*)* ')'
    ;

argument
    : NUMBER_LITERAL
    | IDENTIFIER
    ;

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
