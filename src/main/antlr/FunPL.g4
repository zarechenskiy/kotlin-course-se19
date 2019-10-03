grammar FunPL;

file : block;

block : (statement)*;

blockWithBraces : '{' block '}';

statement
    : function (COMMENT)?
    | variable (COMMENT)?
    | expression (COMMENT)?
    | whileExp (COMMENT)?
    | ifExp (COMMENT)?
    | assignment (COMMENT)?
    | returnExp (COMMENT)?
    | COMMENT;

function : 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces;

variable
    : 'var' varName=IDENTIFIER ('=' exp=expression)?;

parameterNames : ((IDENTIFIER ',')* IDENTIFIER)?;

whileExp : 'while' '(' expression ')' blockWithBraces;

ifExp : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

assignment : IDENTIFIER '=' expression;

returnExp : 'return' expression;

expression returns [int value]
    : bin_exp=binaryExpression
    | nbin_exp=nonBinaryExpression;

nonBinaryExpression returns [int value]
    : functionCall
    | IDENTIFIER
    | LITERAL
    | '(' exp=expression ')';

functionCall : IDENTIFIER '(' arguments ')';

arguments : ((expression ',')* expression)?;

binaryExpression returns [int value]
    : or_exp=orExpression
    ;
orExpression returns [int value]
    : and_exp=andExpression
    | and_exp=andExpression '||' or_exp=orExpression
    ;

andExpression returns [int value]
    : eq_exp=eqExpression
    | eq_exp=eqExpression '&&' and_exp=andExpression
    ;

eqExpression returns [int value]
    : lm_exp=lessMoreExpression
    | lm_exp=lessMoreExpression '==' eq_exp=eqExpression
    | lm_exp=lessMoreExpression '!=' eq_exp=eqExpression
    ;

lessMoreExpression returns [int value]
    : add_exp=addExpression
    | addExpression '<' lessMoreExpression
    | addExpression '<=' lessMoreExpression
    | addExpression '>' lessMoreExpression
    | addExpression '>=' lessMoreExpression
    ;

addExpression
    : multExpression
    | multExpression '+' addExpression
    | multExpression '-' addExpression
    ;

multExpression
    : nonBinaryExpression
    | nonBinaryExpression '*' multExpression
    | nonBinaryExpression '/' multExpression
    | nonBinaryExpression '%' multExpression
    ;
    // MULT_EXPRESSION
//    | ADD_EXPRESSION
//    | LESS_MORE_EXPRESSION
//    | EQ_EXPRESSION
//    | AND_EXPRESSION
//    | OR_EXPRESSION;

IDENTIFIER : ('A'..'Z' | 'a'..'z') ('A'..'Z' | 'a'..'z' | '0'..'9')*;

LITERAL : '0' | '-'? ('1'..'9') ('0'..'9')*;

WS : (' ' | '\t' | '\r'| '\n') -> skip;

COMMENT : '//' ('A'..'Z' | 'a'..'z' | '0'..'9')*;