grammar Fun;

file :
    block
;

block :
    statement*
;

blockWithBraces :
    '{' block '}'
;

statement :
      function
    | blockWithBraces
    | assignment
    | whileLoop
    | ifCondition
    | returnStatement
    | expression
    | variable
;

function :
    'fun' identifier '(' (identifier',')* identifier? ')' blockWithBraces
;

variable :
    'var' identifier ('=' expression)?
;

whileLoop :
    'while' '(' expression ')' blockWithBraces
;

ifCondition :
    'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
;

assignment :
    identifier '=' expression
;

returnStatement :
    'return' expression
;

functionCall :
    identifier '(' (expression',')* expression? ')'
;

binaryExpression :
    orExpression
;

orExpression :
    andExpression OR_OPERATION orExpression
    | andExpression
;

andExpression :
    compareExpression AND_OPERATION andExpression
    | compareExpression
;

compareExpression :
    summingExpression COMPARE_OPERATION compareExpression
    | summingExpression
;

summingExpression :
    multiplicatingExpression SUMMING_OPERATION summingExpression
    | multiplicatingExpression
;

multiplicatingExpression :
    singleExpression MULTIPLICATING_OPERATION multiplicatingExpression
    | singleExpression
;

expression :
  binaryExpression
;

singleExpression :
      functionCall
    | identifier
    | numberLiteral
    | expressionWithBraces
;

expressionWithBraces :
    '(' expression ')'
;

identifier :
    InternalIdentifier
;

numberLiteral :
    InternalNumberLiteral
;

InternalIdentifier :
    IDENTIFIER
;

InternalNumberLiteral :
    NUMBER_LITERAL
;

IDENTIFIER :
    ([a-zA-Z])([0-9a-zA-z])*
;

NUMBER_LITERAL :
      '0'
    | '-'? ([1-9])([0-9])*
;

COMMENT :
    '//'.*?'\n'
;


AND_OPERATION : '&&';
OR_OPERATION : '||';
COMPARE_OPERATION : '<' | '<=' | '>' | '>=' | '==' | '!=';
SUMMING_OPERATION : '+' | '-';
MULTIPLICATING_OPERATION : '*' | '/' | '%';

WS : ( COMMENT | ' ' | '\t' | '\r' | '\n' ) -> skip;


