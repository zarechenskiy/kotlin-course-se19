package ru.hse.spb

class InterpreterException(val line: Int, message: String, cause: Throwable) : Exception(message, cause)
