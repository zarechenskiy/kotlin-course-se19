package ru.hse.spb.funterpreter

import java.util.*

class FunException(val description : String, val backtrace : Stack<String> = Stack()) {
    override fun toString(): String {
        return description + backtrace.fold(StringBuilder()) { builder, str ->
            builder.append('\n').append(str)
        }.toString()
    }
}