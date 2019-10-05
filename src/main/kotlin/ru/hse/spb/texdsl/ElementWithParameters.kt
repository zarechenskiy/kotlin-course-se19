package ru.hse.spb.texdsl

import java.io.PrintWriter

abstract class ElementWithParameters(private val compulsoryParameters: Array<out Any>?): Element() {
    private var optionalParameters: Array<out Any>? = null

    private fun printParameters(parameters: Array<out Any>?, beginBracket: String, endBracket: String, writer: PrintWriter) {
        if (parameters != null && parameters.isNotEmpty()) {
            if (parameters[0] !is String && parameters[0] !is Pair<*, *>) {
                throw TexException("Parameters should be either String or Pair<String, String> ")
            }

            printText(beginBracket, writer)

            for (i in parameters.indices) {
                val parameter = parameters[i]
                if (parameter is Pair<*, *>) {
                    printText(parameter.first.toString() + "=" + parameter.second.toString(), writer)
                } else {
                    printText(parameter.toString(), writer)
                }

                if (i + 1 < parameters.size) {
                    printText(",", writer)
                }
            }

            printText(endBracket, writer)
        }
    }

    fun printParameters(writer: PrintWriter) {
        printCompulsoryParameters(writer)
        printOptionalParameters(writer)
    }

    fun addOptionalParameters(vararg optionalParameters: Any) {
        if (optionalParameters.isEmpty()) {
            return
        }

        if (this.optionalParameters != null && this.optionalParameters!!.isNotEmpty()) {
            throw TexException("There cannot be several optional parameters")
        }

        this.optionalParameters = optionalParameters
    }

    protected fun printCompulsoryParameters(writer: PrintWriter) {
        printParameters(compulsoryParameters, "{", "}", writer)
    }

    protected fun printOptionalParameters(writer: PrintWriter) {
        printParameters(optionalParameters, "[", "]", writer)
    }
}