grammar Fun;

file:
    block EOF
;

block:
    statement*
;

blockWithBraces:
    '{' block '}'
;

statement:
      function
    | variable
    | expression
    | while_
    | if_
    | assignment
    | return_
;

function:
    'fun' name=IDENTIFIER '(' parameterNames ')' body=blockWithBraces
;

variable:
    'var' name=IDENTIFIER ('=' initializer=expression)?
;

parameterNames:
      IDENTIFIER (',' IDENTIFIER)*
    | /* empty */
;

while_:
    'while' '(' condition=expression ')' body=blockWithBraces
;

if_:
    'if' '(' condition=expression ')' mainBranch=blockWithBraces ('else' elseBranch=blockWithBraces)?
;

assignment:
    name=IDENTIFIER '=' value=expression
;

return_:
    'return' expression
;

expression:
      simpleExpression
    | lhs=expression (op = ('*' | '/' | '%')) rhs=expression
    | lhs=expression (op = ('+' | '-')) rhs=expression
    | lhs=expression (op = ('<' | '>' | '<=' | '>=')) rhs=expression
    | lhs=expression (op = ('==' | '!=')) rhs=expression
    | lhs=expression (op = '&&') rhs=expression
    | lhs=expression (op = '||') rhs=expression
;

simpleExpression:
    functionCall
    | IDENTIFIER
    | LITERAL
    | '(' expression ')'
;

functionCall:
    name=IDENTIFIER '(' arguments ')'
;

arguments:
      expression (',' expression)*
    | /* empty */
;

IDENTIFIER:
    [A-Z_a-z][0-9A-Z_a-z]*
;

LITERAL:
    '0'|[1-9][0-9]*
;

WHITESPACE:
    [ \t\r\n]+ -> channel(HIDDEN)
;