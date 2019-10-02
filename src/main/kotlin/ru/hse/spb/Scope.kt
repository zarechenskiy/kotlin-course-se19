package ru.hse.spb

class Scope(
        val parent: Scope? = null,
        private val mapToValue: MutableMap<String, Int> = mutableMapOf(),
        private val mapToFun: MutableMap<String, Function> = mutableMapOf()
) {

    fun getVar(name: String): Int? = mapToValue.getOrElse(name) { parent?.getVar(name) }
    fun defVar(name: String, value: Int) {
        mapToValue[name] = value
    }

    fun assignVar(name: String, value: Int): Boolean =
            if (mapToValue.containsKey(name)) {
                mapToValue[name] = value
                true
            } else {
                parent?.assignVar(name, value) ?: false
            }


    fun getFun(name: String): Function? = mapToFun.getOrElse(name) { parent?.getFun(name) }
    fun defFun(name: String, value: UserFunction) {
        mapToFun[name] = value
    }

    fun copy() = Scope(parent, HashMap(mapToValue), HashMap(mapToFun))

    companion object {
        fun createRootScope() = Scope(mapToFun = mutableMapOf(Std.PrintLn.name to Std.PrintLn))
    }
}
