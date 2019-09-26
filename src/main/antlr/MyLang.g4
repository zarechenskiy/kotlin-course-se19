grammar MyLang;

lFile: lBlock;

lBlock: lStatement*;

lBlockWithBraces: '{' lBlock '}';

lStatement: lFun | lVar | lExpr | lWhile | lIf | lAssignment | lReturn;

lFun: 'fun' IDENTIFIER '(' lParams ')' lBlockWithBraces;

lParams: IDENTIFIER (',' IDENTIFIER)* | ;

lVar: 'var' IDENTIFIER  '=' lExpr;

lWhile: 'while' '(' lCond=lExpr ')' lBody=lBlockWithBraces;

lIf: 'if' '(' lCond=lExpr ')' lThen=lBlockWithBraces ('else' lElse=lBlockWithBraces)?;

lAssignment: IDENTIFIER '=' lExpr;

lReturn: 'return' lExpr;

lExpr: lFuncCall                            #lExprCall
    | IDENTIFIER                            #lExprIdentifier

    | '-' lExpr                             #lExprUnaryMinus
    | '+' lExpr                             #lExprUnaryPlus

    | left=lExpr '/' right=lExpr            #lExprBinaryDiv
    | left=lExpr '%' right=lExpr            #lExprBinaryMod
    | left=lExpr '*' right=lExpr            #lExprBinaryMult

    | left=lExpr '+' right=lExpr            #lExprBinaryAdd
    | left=lExpr '-' right=lExpr            #lExprBinarySubtract

    | left=lExpr '<' right=lExpr            #lExprBinaryLess
    | left=lExpr '>' right=lExpr            #lExprBinaryMore
    | left=lExpr '<=' right=lExpr           #lExprBinaryLessEq
    | left=lExpr '>=' right=lExpr           #lExprBinaryMoreEq
    | left=lExpr '==' right=lExpr           #lExprBinaryEq
    | left=lExpr '!=' right=lExpr           #lExprBinaryNeq



    | left=lExpr '&&' right=lExpr           #lExprBinaryAnd
    | left=lExpr '||' right=lExpr           #lExprBinaryOr


    | LOG_CONST                             #lExprConstLogical
    | LITERAL                               #lExprConstLiteral

    | '(' lExpr ')'                         #lExprPar;

lFuncCall: IDENTIFIER '(' lArgs ')';

lArgs: lExpr (',' lExpr)* | ;

LOG_CONST: 'true' | 'false';

WS : (' ' | '\t' | '\r'| '\n') -> skip;
CM : '//' ~[\n]* -> skip;

IDENTIFIER: [a-zA-Z][a-zA-Z0-9]*;

LITERAL: [1-9][0-9]+ | [0-9];

