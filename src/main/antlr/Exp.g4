grammar Exp;

my_file: my_block;

my_block: my_statement*;

my_block_with_braces: '{' my_block '}';

my_statement: my_fun | my_var | my_expr | my_while | my_if | my_assignment | my_return;

my_fun: 'fun' IDENTIFIER '(' my_params ')' my_block_with_braces;

my_params: IDENTIFIER (',' IDENTIFIER)* | ;

my_var: 'var' (IDENTIFIER | my_assignment);

my_while: 'while' '(' my_expr ')' my_block_with_braces;

my_if: 'if' '(' my_expr ')' my_block_with_braces ('else' my_block_with_braces)?;

my_assignment: IDENTIFIER '=' my_expr;

my_return: 'return' my_expr;

my_expr: my_func_call | IDENTIFIER | ALL_UNAR_OP my_expr |  ALL_CONST | LITERAL
                      | '(' my_expr ')' | my_expr ALL_BIN_OP my_expr;

my_func_call: IDENTIFIER '(' my_args ')';

my_args: my_expr (',' my_expr)* | ;

ALL_UNAR_OP: '-' | '+';

ALL_BIN_OP: MULT_OP | ADD_OP | COMP_OP | LOG_OP;

ALL_CONST: LOG_CONST;

MULT_OP: '*' | '/' | '%';
ADD_OP: '+' | '-';
COMP_OP: '<' | '>' | '<=' | '>=' | '==' | '!=';
LOG_OP: '&&' | '||';
LOG_CONST: 'TRUE' | 'FALSE';

WS : (' ' | '\t' | '\r'| '\n') -> skip;

IDENTIFIER: [a-zA-Z][a-zA-Z0-9]*;

LITERAL: [1-9][0-9]+ | [0-9];

