grammar Grammar;

file
    : block
    ;

block
    : (NEWLINE* statements+=statement NEWLINE*)*
    ;

blockWithBraces
    : '{' block '}'
    ;

function
    : 'fun ' IDENTIFIER '('parameterNames ')' blockWithBraces
    ;

parameterNames
    : (parameterNamesList+=IDENTIFIER (',' parameterNamesList+=IDENTIFIER)*)?
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' ifBlock=blockWithBraces ('else' elseBlock=blockWithBraces)?
    ;

assignment
    : variable '=' expression
    ;

returnStatement
    : 'return' expression
    ;

expression
    : binaryExpression
    ;

statement
    : function
    | returnStatement
    | whileStatement
    | ifStatement
    | variableDefinition
    | expression
    | assignment

    ;

binaryExpression
    : logicalOrExpression
    ;

logicalOrExpression
    : inside=logicalAndExpression
    | left=logicalOrExpression '||' right=logicalAndExpression
    ;

logicalAndExpression
    : inside=equalityExpression
    | left=logicalAndExpression '&&' right=equalityExpression
    ;

equalityExpression
	: inside=relationalExpression
	| left=equalityExpression equalityOperation right=relationalExpression
	;

equalityOperation
    : EQ
    | NEQ
    ;

relationalExpression
	: inside=additiveExpression
	| left=relationalExpression relationalOperation  right=additiveExpression
	;

relationalOperation
    : GE
    | LE
    | LT
    | GT
    ;

additiveExpression
	: inside=multiplicativeExpression
	| left=additiveExpression additiveOperation right=multiplicativeExpression
	;

additiveOperation
    : PLUS
    | MINUS
    ;

multiplicativeOperation
    :
    | MOD
    | MULT
    | DIV
    ;

multiplicativeExpression
    : inside=primaryExpression
    | left=multiplicativeExpression multiplicativeOperation right=primaryExpression
    ;

primaryExpression
    : functionCall
    | variable
    | number
    | '(' expression ')'
    ;

variableDefinition
    : 'var' IDENTIFIER ('=' expression)?
    ;

functionCall
    : IDENTIFIER '(' arguments? ')'
    ;

arguments
    : argumentList+=expression (',' argumentList+=expression)*
    ;

variable
    : IDENTIFIER
    ;

number
    : NUMBER_LITERAL
    ;
// ------------- RULES FOR LEXER -------------

IDENTIFIER
    : [a-zA-Z_]([a-zA-Z0-9_])*
    ;

NUMBER_LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES
    : [ \n]+ -> skip
    ;

COMMENT
    :   '//'(~'\n')* -> skip
    ;


EQ  : '==' ;
NEQ : '!=' ;
GT  : '>'  ;
LT  : '<'  ;
GE  : '>=' ;
LE  : '<=' ;
PLUS : '+' ;
MINUS : '-' ;
MOD : '%' ;
MULT : '*' ;
DIV : '/' ;