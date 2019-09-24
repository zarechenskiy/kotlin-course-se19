package ru.hse.spb.funterpreter

import java.util.*

class FunException(val description : String, val backtrace : Stack<String> = Stack()) {
    override fun toString(): String {
        return description + backtrace.fold(StringBuilder()) { builder, str ->
            builder.append('\n').append(str)
        }.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FunException) {
            return false
        }
        return description == other.description && backtrace == other.backtrace
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + backtrace.hashCode()
        return result
    }
}