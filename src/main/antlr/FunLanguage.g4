grammar FunLanguage;

file : block ;
block : (statements+=statement)* ;
block_with_braces : '{' block '}' ;
statement : function | variable | expression | r_while | r_if | assignment | r_return ;
function : 'fun' name '(' parameter_names ')' block_with_braces ;
variable : 'var' name ('=' expression)? ;
parameter_names : (names+=name (',' names+=name)*)? ;
r_while : 'while' '(' expression ')' block_with_braces ;
r_if : 'if' '(' expression ')' block_with_braces ('else' block_with_braces)? ;
assignment : name '=' expression ;
r_return : 'return' expression ;
expression : function_call | binary_expression | name | constant | '(' expression ')' ;
function_call : name '(' arguments ')' ;
arguments : (expressions+=expression (',' expressions+=expression)*)? ;
name : IDENTIFIER ;
constant : LITERAL ;

binary_expression : or_expression ;
or_expression : expressions+=and_expression ('||' expressions+=and_expression)* ;
and_expression : expressions+=equal_expression ('&&' expressions+=equal_expression)* ;
equal_expression : expressions+=less_expression (ops+=('==' | '!=') expressions+=less_expression)* ;
less_expression : expressions+=sum_expression (ops+=('<' | '<=' | '>' | '>=') expressions+=sum_expression)* ;
sum_expression : expressions+=mult_expression (ops+=('+' | '-') expressions+=mult_expression)* ;
mult_expression :  expressions+=base_binary_expression (ops+=('*' | '/' | '%') expressions+=base_binary_expression)* ;
base_binary_expression : function_call | name | constant | '(' expression ')' ;

IDENTIFIER : [_a-zA-Z][_a-zA-Z0-9]* ;
LITERAL : '0'|('-')?([1-9])([0-9])* ;
COMMENT : '//' (~[\n\r])* -> channel(HIDDEN);
WS : [ \n\r]+ -> channel(HIDDEN);