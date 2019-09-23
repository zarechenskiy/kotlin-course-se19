grammar FunLanguage;

file : block ;
block : (statement)* ;
block_with_braces : '{' block '}' ;
statement : function | variable | expression | r_while | r_if | assignment | r_return ;
function : 'fun' IDENTIFIER '(' parameter_names ')' block_with_braces ;
variable : 'var' IDENTIFIER ('=' expression)? ;
parameter_names : (IDENTIFIER (',' IDENTIFIER)*)? ;
r_while : 'while' '(' expression ')' block_with_braces ;
r_if : 'if' '(' expression ')' block_with_braces ('else' block_with_braces)? ;
assignment : IDENTIFIER '=' expression ;
r_return : 'return' expression ;
expression : function_call | binary_expression | IDENTIFIER | LITERAL | '(' expression ')' ;
function_call : IDENTIFIER '(' arguments ')' ;
arguments : (expression (',' expression)*)? ;

binary_expression : or_expression ;
or_expression : and_expression ('||' and_expression)* ;
and_expression : equal_expression ('&&' equal_expression)* ;
equal_expression : less_expression (('==' | '!=') less_expression)* ;
less_expression : sum_expression (('<' | '<=' | '>' | '>=') sum_expression)* ;
sum_expression : mult_expression (('+' | '-') mult_expression)* ;
mult_expression :  base_binary_expression (('*' | '/' | '%') base_binary_expression)* ;
base_binary_expression : function_call | IDENTIFIER | LITERAL | '(' expression ')' ;

IDENTIFIER : [_a-zA-Z][_a-zA-Z0-9]* ;
LITERAL : '0'|('-')?([1-9])([0-9])* ;
SPACES: [ \n\t\r]+ -> skip;