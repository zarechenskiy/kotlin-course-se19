grammar FunCall;

file
    : block
    ;

block
    : (NEWLINE* statements+=statement NEWLINE*)*
    ;

blockWithBraces
    : '{' SPACES? block  SPACES? '}'
    ;

statement
    : function
    | variable
    | expression
    | whileBlock
    | ifBlock
    | assignment
    | returnBlock
    ;

function
    : 'fun' IDENTIFIER SPACES? '(' parameterNames? ')' SPACES? blockWithBraces
    ;

variable
    : 'var' SPACES IDENTIFIER SPACES? ('=' SPACES? expression)?
    ;

parameterNames
    : IDENTIFIER SPACES? (',' SPACES? IDENTIFIER SPACES?)*
    ;

whileBlock
    : 'while' SPACES? '(' expression ')' SPACES? blockWithBraces
    ;

ifBlock
    : 'if' SPACES? '(' expression ')' SPACES? blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

expression
    : functionCall
    | '(' SPACES? expression SPACES? ')'
    // binariExpression
    | expression SPACES? op+=('*' | '/' | '%') SPACES? expression
    | expression SPACES? op+=('+' | '-') SPACES? expression
    | expression SPACES? op+=('<' | '>' | '<=' | '>=') SPACES? expression
    | expression SPACES? op+=('!=' | '==') SPACES? expression
    | expression SPACES? op+='&&' SPACES? expression
    | expression SPACES? op+='||' SPACES? expression
    //
    | IDENTIFIER
    | NUMBER_LITERAL
    ;

functionCall
    : IDENTIFIER '(' arguments? ')'
    ;

arguments
    : expression SPACES? (',' SPACES? expression SPACES?)*
    ;

returnBlock
    : 'return' SPACES expression
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

// https://en.cppreference.com/w/c/language/operator_precedence
//BINART_OPERATORS_PRECEDENCE3
//    : ('*' | '/' | '%')
//    ;
//
//BINART_OPERATORS_PRECEDENCE4
//    : ('+' | '-') SPACES*
//    ;
//
//BINART_OPERATORS_PRECEDENCE6
//    : ('<' | '>') '='?
//    ;
//
//BINART_OPERATORS_PRECEDENCE7
//    : ('!' | '=') '='
//    ;
//
//BINART_OPERATORS_PRECEDENCE11
//    : '&&'
//    ;
//BINART_OPERATORS_PRECEDENCE12
//    : '||'
//    ;