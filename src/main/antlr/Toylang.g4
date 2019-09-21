grammar Toylang;

file
    : block
    ;

block
    : statement*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : stmtFunction
    | stmtVariable
    | stmtExpression
    | stmtWhile
    | stmtIf
    | stmtAssigment
    | stmtReturn
    | stmtComment
    ;

stmtComment
    : COMMENT
    ;

stmtFunction
    : KW_FUN IDENTIFIER '(' functionParameterNames ')' blockWithBraces
    ;

stmtVariable
    : KW_VAR IDENTIFIER ('=' stmtExpression)?
    ;

functionParameterNames
    : (parameters += IDENTIFIER)(',' parameters += IDENTIFIER)*
    | /* epsilon */
    ;

stmtWhile
    : KW_WHILE '(' stmtExpression ')' blockWithBraces
    ;

stmtIf
    : KW_IF '(' stmtExpression ')' (branchThen = blockWithBraces) (KW_ELSE branchElse = blockWithBraces)?
    ;

stmtAssigment
    : IDENTIFIER '=' stmtExpression
    ;

stmtReturn
    : KW_RETURN stmtExpression
    ;

stmtExpression
    : binaryExpression
    ;

functionCall
    : IDENTIFIER '(' functionArguments ')'
    ;

functionArguments
    : stmtExpression (',' stmtExpression)*
    | /* epsilon */
    ;

binaryExpression
    : logicalExpression
    ;

logicalExpression
    : eqExpression (OP_LOGICAL eqExpression )*
    ;

eqExpression
    : compareExpression (OP_EQ compareExpression )*
    ;

compareExpression
    : additionExpression (OP_COMPARE additionExpression )*
    ;

additionExpression
    : multiplyExpression (OP_ADDITIONAL multiplyExpression )*
    ;

multiplyExpression
    : atomExpression (OP_MULTIPLY atomExpression )*
    ;

atomExpression
    : functionCall
    | identifier
    | integralLiteral
    | '(' stmtExpression ')'
    ;

identifier : IDENTIFIER;

integralLiteral : INTEGER_LITERAL;

KW_RETURN
    : 'return'
    ;

KW_ELSE
    : 'else'
    ;

KW_IF
    : 'if'
    ;

KW_FUN
    : 'fun'
    ;

KW_VAR
    : 'var'
    ;

KW_WHILE
    : 'while'
    ;

OP_LOGICAL
    : ('||'|'&&')
    ;

OP_EQ
    : ('=='|'!=')
    ;

OP_ADDITIONAL
    : [+-]
    ;

OP_COMPARE
    : '<'|'>'|'<='|'>='
    ;

OP_MULTIPLY
    : ('*'|'/'|'%')
    ;

IDENTIFIER
    : ([a-zA-Z])+
    ;

INTEGER_LITERAL
    : '-'? '0'
    | '-'? ([1-9])([0-9])*
    ;

COMMENT
    : '//'(~[\r\n])*
    ;

WS : (' ' | '\t' | '\n') -> skip;
