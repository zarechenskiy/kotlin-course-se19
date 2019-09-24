grammar Lang;

WS                 : ('\r\n' | '\r' | '\n' | ' ' | '\t') ;

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


file                         : block ;

statement                     : function
                             | assignment
                             | expression
                             | variable
                             | while_expr
                             | if_expr
                             | return_expr ;

block                        : (statement (WS+ statement)*)? ;

block_with_braces            : WS* LBRACE WS* block WS* RBRACE ;

function                     : WS* FUNCTION WS+ IDENTIFIER WS* LPAREN WS* parameter_names WS* RPAREN WS* block_with_braces ;

variable                     : WS* VARIABLE WS+ IDENTIFIER (WS* ASSIGNMENT WS* expression)? ;

parameter_names              : WS* (IDENTIFIER (WS* SEPARATOR WS* IDENTIFIER)*)? ;

while_expr                   : WS* WHILE WS* LPAREN WS* expression WS* RPAREN WS* block_with_braces ;

if_expr                      : WS* IF WS* LPAREN WS* expression WS* RPAREN WS* block_with_braces (WS* ELSE WS* block_with_braces)? ;

assignment                   : WS* IDENTIFIER WS* ASSIGNMENT WS* expression ;

return_expr                  : WS* RET WS+ expression ;

expression                   : WS* function_call
                             | WS* IDENTIFIER
                             | WS* LITERAL
                             | WS* LPAREN WS* expression WS* RPAREN
                             | WS* binary_expression ;

function_call                : WS* IDENTIFIER WS* LPAREN WS* arguments WS* RPAREN ;

arguments                    : WS* (expression (WS* SEPARATOR WS* expression)*) ;

binary_expression            : WS* LITERAL
                             | WS* IDENTIFIER
                             | WS* LPAREN WS* middle=binary_expression WS* RPAREN
                             | left=binary_expression WS* operator=(ASTERISK | DIVISION | REMINDER) WS* right=binary_expression
                             | left=binary_expression WS* operator=(MINUS | PLUS) WS* right=binary_expression
                             | left=binary_expression WS* operator=(LT | GT | LE | GE) WS* right=binary_expression
                             | left=binary_expression WS* operator=(EQ | NEQ) WS* right=binary_expression
                             | left=binary_expression WS* operator=AND WS* right=binary_expression
                             | left=binary_expression WS* operator=OR WS* right=binary_expression ;
