grammar FunPL;

file : block {
    import java.util.*;
    HashMap<String, Integer> variables = new HashMap<>();
};

block : (statement)*;

blockWithBraces : ('{' block '}')*;

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
    : 'var' varName=IDENTIFIER ('=' exp=expression)? {
        variables.put($varName.text, Integer.parseInt($exp.value));
    }
    ;

parameterNames : ((IDENTIFIER ',')* IDENTIFIER)?;

whileExp : 'while' '(' expression ')' blockWithBraces;

ifExp : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

assignment : IDENTIFIER '=' expression;

returnExp : 'return' expression;

expression returns [int value]
    : bin_exp=binaryExpression {$value = $bin_exp.value}
    | nbin_exp=nonBinaryExpression {$value = $nbin_exp.value};

nonBinaryExpression returns [int value]
    : functionCall {$value = 0}
    | IDENTIFIER {$value = 0}
    | LITERAL {$value = 0}
    | '(' exp=expression ')' { $value = $exp.value };

functionCall : IDENTIFIER '(' arguments ')';

arguments : ((expression ',')* expression)?;

binaryExpression returns [int value]
    : or_exp=orExpression { $value = $or_exp.value }
    ;
orExpression returns [int value]
    : and_exp=andExpression { $value = $and_exp.value }
    | and_exp=andExpression '||' or_exp=orExpression { $value = 0 }
    ;

andExpression returns [int value]
    : eq_exp=eqExpression { $value = $eq_exp.value }
    | eq_exp=eqExpression '&&' and_exp=andExpression { $value = $eq_exp.value && $and_exp.value }
    ;

eqExpression returns [int value]
    : lm_exp=lessMoreExpression { $value = $lm_exp.value }
    | lm_exp=lessMoreExpression '==' eq_exp=eqExpression { $value = 0 }
    | lm_exp=lessMoreExpression '!=' eq_exp=eqExpression { $value = 0 }
    ;

lessMoreExpression returns [int value]
    : add_exp=addExpression { $value = 0}
    | addExpression '<' lessMoreExpression { $value = 0 }
    | addExpression '<=' lessMoreExpression { $value = 0 }
    | addExpression '>' lessMoreExpression { $value = 0 }
    | addExpression '>=' lessMoreExpression { $value = 0 }
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