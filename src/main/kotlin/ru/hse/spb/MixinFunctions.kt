package ru.hse.spb

object MixinFunctions {
    fun isMixin(name: String) = name == "println"
    fun execute(name: String, params: Collection<Context>): Context {
        println(params.map { it.value }.joinToString(separator = " "))
        return Context.NONE;
    }
}
