grammar FunPL;

file : block;

block : statement? (NEWLINE statement)* NEWLINE?;

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

ifExp : 'if' '(' expression ')' branch1=blockWithBraces ('else' branch2=blockWithBraces)?;

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
    : and_exp0=andExpression
    | and_exp1=andExpression '||' or_exp1=orExpression
    ;

andExpression returns [int value]
    : eq_exp0=eqExpression
    | eq_exp1=eqExpression '&&' and_exp1=andExpression
    ;

eqExpression returns [int value]
    : lm_exp0=lessMoreExpression
    | lm_exp1=lessMoreExpression '==' eq_exp1=eqExpression
    | lm_exp2=lessMoreExpression '!=' eq_exp2=eqExpression
    ;

lessMoreExpression returns [int value]
    : add_exp0=addExpression
    | add_exp1=addExpression '<' lm_exp1=lessMoreExpression
    | add_exp2=addExpression '<=' lm_exp2=lessMoreExpression
    | add_exp3=addExpression '>' lm_exp3=lessMoreExpression
    | add_exp4=addExpression '>=' lm_exp4=lessMoreExpression
    ;

addExpression
    : mult_exp0=multExpression
    | mult_exp1=multExpression '+' add_exp1=addExpression
    | mult_exp2=multExpression '-' add_exp2=addExpression
    ;

multExpression
    : nb_exp0=nonBinaryExpression
    | nb_exp1=nonBinaryExpression '*' mult_exp1=multExpression
    | nb_exp2=nonBinaryExpression '/' mult_exp2=multExpression
    | nb_exp3=nonBinaryExpression '%' mult_exp3=multExpression
    ;
    // MULT_EXPRESSION
//    | ADD_EXPRESSION
//    | LESS_MORE_EXPRESSION
//    | EQ_EXPRESSION
//    | AND_EXPRESSION
//    | OR_EXPRESSION;

IDENTIFIER : ('A'..'Z' | 'a'..'z') ('A'..'Z' | 'a'..'z' | '0'..'9')*;

LITERAL : '0' | '-'? ('1'..'9') ('0'..'9')*;

WS : (' ' | '\t') -> skip;

COMMENT : '//' ('A'..'Z' | 'a'..'z' | '0'..'9')*;

NEWLINE : ('\r' | '\n')+;