grammar FunCall;

file
    : block
    ;

block
//    : ((EMPTY_LINE)* SPACES? statement (EMPTY_LINE)*)*
    : (SPACES? statement SPACES? (NEWLINE | EOF))*
    ;

blockWithBraces
    : '{' SPACES? NEWLINE? SPACES? block SPACES? '}' SPACES? NEWLINE?
    ;

statement
    : variable
    | expression
    | whileBlock
    | ifBlock
    | assignment
    | function
    | returnBlock
    ;

function
    : 'fun' SPACES IDENTIFIER SPACES? '(' parameterNames? ')' SPACES? blockWithBraces
    ;

variable
    : 'var' SPACES IDENTIFIER SPACES? ('=' SPACES? expression)?
    ;

parameterNames
    : IDENTIFIER SPACES? (',' SPACES? IDENTIFIER SPACES?)*
    ;

whileBlock
    : 'while' SPACES? '(' SPACES? expression SPACES? ')' SPACES? blockWithBraces
    ;

ifBlock
    : 'if' SPACES? '(' SPACES? expression SPACES? ')' SPACES? NEWLINE? blockWithBraces (SPACES? 'else' SPACES? NEWLINE? blockWithBraces)?
    ;

assignment
    : IDENTIFIER SPACES? '=' SPACES? expression
    ;

expression
    : functionCall
    | '(' SPACES? expression SPACES? ')'
    // binariExpression
    | expression SPACES? op=('*' | '/' | '%') SPACES? expression
    | expression SPACES? op=('+' | '-') SPACES? expression
    | expression SPACES? op=('<' | '>' | '<=' | '>=') SPACES? expression
    | expression SPACES? op=('!=' | '==') SPACES? expression
    | expression SPACES? op='&&' SPACES? expression
    | expression SPACES? op='||' SPACES? expression
    //
    | IDENTIFIER
    | NUMBER_LITERAL
    ;

functionCall
    : IDENTIFIER SPACES? '(' SPACES? arguments? SPACES? ')'
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
    : ('\r\n'|'\n'|'\r')
    ;

SPACES
    : ' '+
    ;

//EMPTY_LINE
////    : SPACES? COMMENT? (NEWLINE | EOF)
//    : (NEWLINE | EOF)
//    ;

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