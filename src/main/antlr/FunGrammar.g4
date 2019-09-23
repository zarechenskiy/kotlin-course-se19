grammar FunGrammar;

file : block;

block
    : (NEWLINE* statement+ NEWLINE*)*
    ;

block_with_braces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | l_while
    | l_if
    | assignment
    | l_return
    ;

function
    : 'fun' identifier '(' parameter_names ')' block_with_braces
    ;

variable
    : 'var' identifier ('=' expression)?
    ;

parameter_names
    : identifier
      (',' identifier)*
    ;

l_while
    : 'while' '(' expression ')' block_with_braces
    ;

l_if
    : 'if' '(' expression ')' block_with_braces
      ( 'else' block_with_braces )?
    ;

assignment
    : variable '=' expression
    ;

l_return
    : 'return' expression
    ;

expression
    : binary_expression
    ;

function_call
    : identifier '(' arguments ')'
    ;

arguments
    : expression
      (',' expression)*
    ;

binary_expression
    : or
    ;

or
    : and
    | or OR_OPERATOR and
    ;

and
    :  equality
    | and AND_OPERATOR equality
    ;

equality
    : add_substract
    equality EQ_OPERATOR add_substract
    ;

add_substract
    : mult_divide
    | add_substract ('+' | '-') mult_divide
    ;

mult_divide
    : singular_expression
    | mult_divide ('*' | '/' | '%') singular_expression
    ;

singular_expression
    : function_call
    | identifier
    | LITERAL
    | '(' expression ')'
    ;

identifier
    : IDENTIFIER
    ;

IDENTIFIER
    : [a-zA-Z][a-zA-Z0-9]+
    ;

LITERAL
    :   ('0' | ('1'..'9') ('0'..'9')*)
    ;

WS : ('\t' | ' ' | '\r' | '\n') -> skip;

NEWLINE : '\n';

OR_OPERATOR : '||';
AND_OPERATOR : '&&';
EQ_OPERATOR : [><] | '>=' | '<=' | '==' | '!=';
ADD_OPERATOR : [+-];
MULT_OPERATOR : [*/%];
