grammar Lang;

WS : ('\r\n' | '\r' | '\n') -> skip ;

VARIABLE           : 'var' ;
FUNCTION           : 'fun' ;
WHILE              : 'while' ;
IF                 : 'if' ;
ELSE               : 'else' ;
RET                : 'ret' ;

IDENTIFIER         : [a-zA-Z_][a-zA-Z0-9_]* ;

LITERAL            : '0'|[1-9][0-9]* ;

SEPARATOR          : ','(' ')* ;

PLUS               : '+' ;
MINUS              : '-' ;
ASTERISK           : '*' ;
DIVISION           : '/' ;
REMINDER           : '%' ;

ASSIGNMENT         : '=' ;

LPAREN             : '(' ;
RPAREN             : ')' ;

LBRACE             : '{' ;
RBRACE             : '}' ;


LT                 : '<' ;
GT                 : '>' ;
LE                 : '<=' ;
GE                 : '>=' ;
EQ                 : '==' ;
NEQ                : '!=' ;

OR                 : '||' ;
AND                : '&&' ;

ERROR              : . ;


file                         : block ;

statement                     : function
                             | variable
                             | expression
                             | while_expr
                             | if_expr
                             | assignment
                             | return_expr ;

block                        : (statement)* ;

block_with_braces            : LBRACE block RBRACE ;

function                     : FUNCTION IDENTIFIER LPAREN parameter_names RPAREN block_with_braces ;

variable                     : VARIABLE IDENTIFIER (ASSIGNMENT expression)? ;

parameter_names              : (IDENTIFIER (SEPARATOR IDENTIFIER)*)? ;

while_expr                   : WHILE LPAREN expression RPAREN block_with_braces ;

if_expr                      : IF LPAREN expression RPAREN block_with_braces (ELSE block_with_braces)? ;

assignment                   : IDENTIFIER ASSIGNMENT expression ;

return_expr                  : RET expression ;

expression                   : function_call
                             | IDENTIFIER
                             | LITERAL
                             | LPAREN expression RPAREN
                             | binary_expression ;

function_call                : IDENTIFIER LPAREN arguments RPAREN ;

arguments                    : (expression | expression (SEPARATOR expression))? ;

binary_expression            : LITERAL
                             | IDENTIFIER
                             | left=binary_expression operator=(ASTERISK | DIVISION | REMINDER) right=binary_expression
                             | left=binary_expression operator=(MINUS | PLUS) right=binary_expression
                             | left=binary_expression operator=(LT | GT | LE | GE) right=binary_expression
                             | left=binary_expression operator=(EQ | NEQ) right=binary_expression
                             | left=binary_expression operator=AND right=binary_expression
                             | left=binary_expression operator=OR right=binary_expression ;
