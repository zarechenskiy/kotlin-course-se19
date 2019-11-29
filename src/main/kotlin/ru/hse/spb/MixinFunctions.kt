package ru.hse.spb

object MixinFunctions {
    fun isMixin(name: String) = name == "println"

    fun execute(name: String, params: Collection<Context>): Context {
        when (name) {
            "println" -> println(params.map { it.value }.joinToString(separator = " "))
            else -> error("Unknown mixin function")
        }

        return Context.NONE
    }
}
