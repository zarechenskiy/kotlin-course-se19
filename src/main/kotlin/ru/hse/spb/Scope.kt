package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext

/**
 * Map that returns object of type T from the given string name in the given scope or it's parent scope.
 */
class Scope<T>(private val parentScope: Scope<T>?, private var scopedType: String? = null) {
    private val scoped = mutableMapOf<String, T?>()

    init {
        if (parentScope != null) {
            scopedType = parentScope.scopedType
        }
    }

    fun getScoped(identifier: String, context: ParserRuleContext): T? {
        val local = scoped[identifier]

        if (local != null) {
            return local
        }

        if (parentScope != null) {
            val outer = parentScope.getScoped(identifier, context)

            if (outer != null) {
                return outer
            }
        }

        throw CompileException(context, "unknown $scopedType")
    }

    fun update(identifier: String, value: T?, context: ParserRuleContext) {
        if (scoped.containsKey(identifier)) {
            scoped[identifier] = value
        } else {
            if (parentScope != null) {
                parentScope.update(identifier, value, context)
            } else {
                throw CompileException(context, "unknown $scopedType")
            }
        }
    }

    fun insert(identifier: String, value: T?, context: ParserRuleContext) {
        if (scoped.containsKey(identifier)) {
            throw CompileException(context, "duplicate $scopedType definition")
        }

        scoped[identifier] = value
    }
}