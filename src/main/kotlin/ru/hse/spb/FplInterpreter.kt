package ru.hse.spb

import java.io.PrintStream
import java.util.*

class Env<T>(private val env: MutableMap<String, T>) {
    fun containsName(name: String): Boolean = env.containsKey(name)
    fun set(name: String, value: T) {
        env[name] = value
    }

    fun get(name: String): T? = env[name]
}

class FplEnv(val varEnv: Env<Optional<Int>>, val funEnv: Env<Fun>) {

    constructor() : this(Env<Optional<Int>>(mutableMapOf()), Env<Fun>(mutableMapOf()))

    // A hack for returning from functions.
    var returnFlag = false

    fun setVar(name: String, value: Optional<Int>) = varEnv.set(name, value)
    fun getVar(name: String): Optional<Int>? = varEnv.get(name)

    fun setFun(name: String, value: Fun) = funEnv.set(name, value)
    fun getFun(name: String): Fun? = funEnv.get(name)
}


class FplParentEnv(private val envs: MutableList<FplEnv>) {

    constructor() : this(mutableListOf())

    fun push(env: FplEnv) = envs.add(0, env)

    fun pop() = envs.removeAt(0)

    fun getVar(ident: String): Optional<Int>? = getCtxWithVar(ident)?.getVar(ident)

    fun getFun(ident: String): Fun? = getCtxWithFun(ident)?.getFun(ident)

    fun containsVar(ident: String): Boolean = envs.filter { it.varEnv.containsName(ident) }.count() > 0

    fun getCtxWithVar(ident: String): FplEnv? = envs.find { it.varEnv.containsName(ident) }
    fun getCtxWithFun(ident: String): FplEnv? = envs.find { it.funEnv.containsName(ident) }

}

class FplInterpreter(val env: FplEnv,
                     val parentEnvs: FplParentEnv,
                     val output: PrintStream) {
    private val builtins = BuiltIns(output).functions

    constructor() : this(FplEnv(), FplParentEnv(), System.out)

    constructor(output: PrintStream) : this(FplEnv(), FplParentEnv(), output)

    fun interpret(tree: FplTree): Int {
        for (stmt in tree.statements) {
            val result = interpretStatement(stmt)
            if (env.returnFlag) {
                return result
            }
        }
        return 0
    }

    private fun interpretStatement(stmt: Statement): Int =
            when (stmt) {
                is If -> interpretIf(stmt)
                is While -> {
                    interpretWhile(stmt); 0
                }
                is Assign -> {
                    interpretAssign(stmt); 0
                }
                is Return -> interpretReturn(stmt)
                is Variable -> {
                    interpretVariable(stmt); 0
                }
                is Fun -> {
                    interpretFun(stmt); 0
                }
                is Expr -> interpretExpr(stmt)
            }

    private fun interpretReturn(ret: Return): Int {
        val res = interpretExpr(ret.expr)
        env.returnFlag = true
        return res
    }

    private fun interpretFun(func: Fun) = env.setFun(func.identifier, func)

    /**
     * Add new definition into the current context.
     */
    private fun interpretVariable(variable: Variable) {
        val expr: Optional<Int> = variable.value.map { expr -> interpretExpr(expr) }
        env.setVar(variable.identifier, expr)
    }

    private fun interpretExpr(expr: Expr): Int =
            when (expr) {
                is Identifier -> interpretIdentifier(expr)
                is Literal -> interpretLiteral(expr)
                is Binary -> interpretBinary(expr)
                is FunCall -> interpretFunCall(expr)
            }

    /**
     * Get definition of an identifier from the current context or
     * try to get it from the parent ones.
     */
    private fun interpretIdentifier(ident: Identifier): Int =
            env.getVar(ident.identifier)?.orElseThrow {
                RuntimeException("Variable ${ident.identifier} doesn't set!")
            } ?: parentEnvs.getVar(ident.identifier)?.orElseThrow {
                RuntimeException("Variable ${ident.identifier} doesn't set!")
            } ?: throw RuntimeException("Variable ${ident.identifier} doesn't exist!")


    /**
     * As we have only numerical literals we don't need to do anything.
     */
    private fun interpretLiteral(literal: Literal): Int = literal.literal


    /**
     * Evaluate just by definition of binary functions.
     */
    private fun interpretBinary(binary: Binary): Int {
        val left = interpretExpr(binary.left)
        val right = interpretExpr(binary.right)
        return binary.op.apply(left, right)
    }

    /**
     * Call a function.
     *
     * Firstly, we look up a function in the builtins and only then in our context.
     */
    private fun interpretFunCall(funCall: FunCall): Int {
        // Argument evaluated in the current context.
        val arguments = funCall.arguments.map { interpretExpr(it) }

        // Look up in the builtins.
        if (builtins.containsKey(funCall.identifier)) {
            val fn = builtins[funCall.identifier]
                    ?: throw RuntimeException("Builtin ${funCall.identifier} doesn't exist!")
            return fn(arguments.toIntArray())
        }

        val func = env.getFun(funCall.identifier)
                ?: parentEnvs.getFun(funCall.identifier)
                ?: throw RuntimeException("Function ${funCall.identifier}")

        if (func.parameters.size != funCall.arguments.size) {
            throw RuntimeException("Wrong number of arguments for function ${funCall.identifier}")
        }

        val newEnv = FplEnv()
        func.parameters.zip(funCall.arguments).map { (param, expr) -> (param to interpretExpr(expr)) }
                .forEach { (param, value) -> newEnv.setVar(param, Optional.of(value)) }

        parentEnvs.push(env)
        val innerInterp = FplInterpreter(newEnv, parentEnvs, output)
        val result = innerInterp.interpret(func.body)
        parentEnvs.pop()
        return result
    }

    /**
     * Interpret `if`.
     *
     * Evaluate condition in the current context.
     *
     * Clause must be evaluated in the fresh context for assignments to stay we're they belong.
     */
    private fun interpretIf(ifClause: If): Int {
        val cond = interpretExpr(ifClause.condition)

        parentEnvs.push(env)
        val innerInterp = FplInterpreter(FplEnv(), parentEnvs, output)

        val result = if (cond.toBoolean()) {
            innerInterp.interpret(ifClause.thenClause)
        } else {
            ifClause.elseClause.map { innerInterp.interpret(it) }.orElse(0)
        }
        parentEnvs.pop()
        env.returnFlag = innerInterp.env.returnFlag
        return result
    }

    /**
     * Interpret `while`
     *
     * For the first time we evaluate condition in our context. Then do it only in the inner context.
     */
    private fun interpretWhile(loop: While) {
        var cond = interpretExpr(loop.condition)

        parentEnvs.push(env)
        val innerInterp = FplInterpreter(FplEnv(), parentEnvs, output)
        while (cond.toBoolean()) {
            innerInterp.interpret(loop.body)
            cond = innerInterp.interpretExpr(loop.condition)
        }
        parentEnvs.pop()
        env.returnFlag = innerInterp.env.returnFlag
    }

    /**
     * Set a new value for an existing variable.
     * If variable is not in the current context try yo set it in one of the parent ones.
     */
    private fun interpretAssign(assign: Assign) {
        val expr = interpretExpr(assign.expr)

        val ctx = when {
            env.varEnv.containsName(assign.identifier) -> env
            parentEnvs.containsVar(assign.identifier) -> parentEnvs.getCtxWithVar(assign.identifier)
                    ?: throw RuntimeException("Surprise exception. We've already checked the variable!")
            else -> throw RuntimeException("Variable ${assign.identifier} doesn't exist! Unable to assign a value.")
        }

        ctx.setVar(assign.identifier, Optional.of(expr))
    }
}

class BuiltIns(val output: PrintStream) {
    private fun println(vararg n: Int): Int {
        output.println(n.joinToString { it.toString() })
        return 0
    }

    val functions = mutableMapOf<String, (IntArray) -> Int>()

    init {
        functions["println"] = fun(args: IntArray): Int = this.println(*args)
    }
}

