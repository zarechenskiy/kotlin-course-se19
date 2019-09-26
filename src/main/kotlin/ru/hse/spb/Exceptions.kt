package ru.hse.spb

import java.lang.RuntimeException

class NoSuchFunctionException(error: String?) : RuntimeException(error)
class NoSuchVariableException(error: String?) : RuntimeException(error)
class IllegalFunctionCall(error: String?) : RuntimeException(error)
class IllegalFunctionName(error: String?) : RuntimeException(error)
class UnsupportedBinaryOperation(error: String?) : RuntimeException(error)