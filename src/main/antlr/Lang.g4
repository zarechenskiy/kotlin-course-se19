grammar Lang;

WS : ('\r\n' | '\r' | '\n' | ' ' | '\t') -> skip ;

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

block_with_braces            : WS LBRACE WS block WS RBRACE WS ;

function                     : WS FUNCTION WS IDENTIFIER WS LPAREN WS parameter_names WS RPAREN WS block_with_braces WS ;

variable                     : WS VARIABLE WS IDENTIFIER WS (WS ASSIGNMENT WS expression)? WS ;

parameter_names              : WS (IDENTIFIER WS (WS SEPARATOR WS IDENTIFIER)*)? WS ;

while_expr                   : WS WHILE WS LPAREN WS expression WS RPAREN WS block_with_braces WS ;

if_expr                      : WS IF WS LPAREN WS expression WS RPAREN WS block_with_braces WS (WS ELSE WS block_with_braces)? WS ;

assignment                   : WS IDENTIFIER WS ASSIGNMENT WS expression WS ;

return_expr                  : WS RET WS expression WS ;

expression                   : WS function_call WS
                             | WS IDENTIFIER WS
                             | WS LITERAL WS
                             | WS LPAREN WS expression WS RPAREN WS
                             | WS binary_expression WS ;

function_call                : WS IDENTIFIER WS LPAREN WS arguments WS RPAREN WS ;

arguments                    : WS (expression | expression WS (SEPARATOR expression))? WS ;

binary_expression            : WS LITERAL WS
                             | WS IDENTIFIER WS
                             | WS left=binary_expression WS operator=(ASTERISK | DIVISION | REMINDER) WS right=binary_expression WS
                             | WS left=binary_expression WS operator=(MINUS | PLUS) WS right=binary_expression WS
                             | WS left=binary_expression WS operator=(LT | GT | LE | GE) WS right=binary_expression WS
                             | WS left=binary_expression WS operator=(EQ | NEQ) WS right=binary_expression WS
                             | WS left=binary_expression WS operator=AND WS right=binary_expression WS
                             | WS left=binary_expression WS operator=OR WS right=binary_expression WS ;
