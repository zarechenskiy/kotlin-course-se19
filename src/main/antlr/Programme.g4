grammar Programme;

//FILE = BLOCK
file
    : block
    ;

//BLOCK = (STATEMENT)*
block
    : NEWLINE* (statements+=statement NEWLINE*)*
    ;

//BLOCK_WITH_BRACES = "{" BLOCK "}"
blockWithBraces
    : '{' block '}'
    ;

//STATEMENT = FUNCTION | VARIABLE | EXPRESSION | WHILE | IF | ASSIGNMENT | RETURN
statement
    : function
    | variable
    | expression
    | brockIf
    | blockWhile
    | assigment
    | returnStatement
    ;

//FUNCTION = "fun" IDENTIFIER "(" PARAMETER_NAMES ")" BLOCK_WITH_BRACES
function
    : 'fun' IDENTIFIER '(' parameters ')' blockWithBraces
    ;

//PARAMETER_NAMES = IDENTIFIER{,}
parameters
    : parameterNames+=IDENTIFIER (',' parameterNames+=IDENTIFIER)*
    ;

//VARIABLE = "var" IDENTIFIER ("=" EXPRESSION)?
variable
    : 'var' IDENTIFIER ('=' expression )?
    ;

//WHILE = "while" "(" EXPRESSION ")" BLOCK_WITH_BRACES
blockWhile
    : 'while' '(' expression ')' whileBlockWithBraces=blockWithBraces
    ;

//IF = "if" "(" EXPRESSION ")" BLOCK_WITH_BRACES ("else" BLOCK_WITH_BRACES)?
brockIf
    : 'if' '(' expression ')' ifBlockWithBraces=blockWithBraces ('else' elseBlockWithBraces=blockWithBraces)?
    ;
//ASSIGNMENT = IDENTIFIER "=" EXPRESSION
assigment
    : IDENTIFIER '=' expression
    ;

//RETURN = "return" EXPRESSION
returnStatement
    : 'return' expression
    ;

//EXPRESSION = FUNCTION_CALL | BINARY_EXPRESSION | IDENTIFIER | LITERAL | "(" EXPRESSION ")"
expression
    : binaryExpression
    ;

//FUNCTION_CALL = IDENTIFIER "(" ARGUMENTS ")"
//ARGUMENTS = EXPRESSION{","}
functionCall
    : IDENTIFIER '(' arguments+=expression (',' arguments+=expression)* ')'
    ;

binaryExpression
    :    logicalOrExp
    ;

logicalOrExp
    :   logicalAndExp
         ( OR logicalAndExp
         )*
    ;

logicalAndExp
     :   equalityExp
          ( AND equalityExp
          )*
     ;

equalityExp
    :   relationalExp
         ( OP_EQUAL relationalExp
         )?
    ;

relationalExp
    :    additionExp
         ( OP_COMPARE additionExp
         )?
    ;

additionExp
    :    multiplyExp
         ( OP_ADD multiplyExp
         )*
    ;

multiplyExp
    :    atomExp
         ( OP_MULT atomExp
         )*
    ;

atomExp
    :    literal
    |    functionCall
    |    identifier
    |    '(' expression ')'
    ;

literal
    :   LITERAL
    ;

identifier
    :   IDENTIFIER
    ;

// ------------- RULES FOR LEXER -------------


OR : '||';

AND : '&&';

OP_EQUAL : (EQ | NEQ);
EQ : '==';
NEQ : '!=';

OP_COMPARE : (GT | LT | GTEQ | LTEQ);
GT : '>';
LT : '<';
GTEQ : '>=';
LTEQ : '<=';

OP_ADD : (PLUS | MINUS);
PLUS : '+';
MINUS : '-';

OP_MULT : (MULT | DIV | MOD);
MULT : '*';
DIV : '/';
MOD : '%';

IDENTIFIER
    : ([a-zA-Z])([a-zA-Z0-9_])*
    ;

LITERAL
    : ('0' | ('1'..'9')+('0'..'9')*)
    ;

NEWLINE
    : [\r\n]+
    ;

SPACES
    : (' ' | '\t' ) -> skip
    ;
