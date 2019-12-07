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

    private fun contains(identifier: String, context: ParserRuleContext): Boolean {
        if (identifier in scoped) {
            return true
        }

        return parentScope?.contains(identifier, context) ?: false
    }

    fun getScoped(identifier: String, context: ParserRuleContext): T? {
        if (!contains(identifier, context)) {
            throw CompileException(context, "unknown $scopedType $identifier")
        }

        if (scoped.containsKey(identifier)) {
            return scoped[identifier]
        }

        return parentScope?.getScoped(identifier, context)
    }

    fun update(identifier: String, value: T?, context: ParserRuleContext) {
        if (scoped.containsKey(identifier)) {
            scoped[identifier] = value
        } else {
            if (parentScope != null) {
                parentScope.update(identifier, value, context)
            } else {
                throw CompileException(context, "unknown $scopedType $identifier")
            }
        }
    }

    fun insert(identifier: String, value: T?, context: ParserRuleContext) {
        if (scoped.containsKey(identifier)) {
            throw CompileException(context, "duplicate $scopedType $identifier definition")
        }

        scoped[identifier] = value
    }
}