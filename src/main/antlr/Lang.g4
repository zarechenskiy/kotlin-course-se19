grammar Lang;

/* parser rules */

file: block;

block: (statement)*;

blockWithBraces: '{' block '}';

statement: function | variable | expression | while_ | if_ | assignment | return_;

function: 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces;

variable: 'var' IDENTIFIER ('=' expression)?;

parameterNames: (IDENTIFIER (',' IDENTIFIER)* )?;

while_: 'while' '(' expression ')' blockWithBraces;

if_: 'if' '(' expression ')' thenBody=blockWithBraces ('else' elseBody=blockWithBraces)?;

assignment: IDENTIFIER '=' expression;

return_: 'return' expression;

expression: nonBinaryExpression | binaryExpression;

nonBinaryExpression: functionCall | identifier | literal | '(' expression ')';

identifier: IDENTIFIER;

literal: LITERAL;

functionCall: IDENTIFIER '(' arguments ')';

arguments: (expression (',' expression)* )?;

binaryExpression: binaryOrExpression;

// LeftA
binaryOrExpression:    single=binaryAndExpression
                   | l=binaryOrExpression   op=OR                  r=binaryAndExpression;

// LeftA
binaryAndExpression:   single=binaryEqualExpression
                   | l=binaryAndExpression  op=AND                 r=binaryEqualExpression;

// NonA
binaryEqualExpression: single=binaryLessExpression
                   | l=binaryLessExpression op=(EQ | NEQ)          r=binaryLessExpression;

// NonA
binaryLessExpression:  single=binaryPlusExpression
                   | l=binaryPlusExpression op=(LE | GE | LT | GT) r=binaryPlusExpression;

// LeftA
binaryPlusExpression:  single=binaryMultExpression
                   | l=binaryPlusExpression op=(PLUS |MINUS)       r=binaryMultExpression;

// LeftA
binaryMultExpression:  single=nonBinaryExpression
                   | l=binaryMultExpression op=(MULT | DIV | MOD)  r=nonBinaryExpression;

/* lexer rules */

IDENTIFIER: [A-Za-z][A-Za-z0-9]*;

LITERAL: '0'|[1-9][0-9]*;

MULT:  '*';
DIV:   '/';
MOD:   '%';

PLUS:  '+';
MINUS: '-';

LE:    '<=';
GE:    '>=';
LT:    '<';
GT:    '>';

EQ:    '==';
NEQ:   '!=';

AND:   '&&';
OR:    '||';


WS: [ \t\r\n]+ -> skip;

COMMENT: '//'~[\n\r]* -> skip;
