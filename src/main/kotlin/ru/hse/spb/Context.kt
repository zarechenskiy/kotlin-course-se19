package ru.hse.spb

class Context(val value: Any?) {
    fun toInt(): Int {
        require(value is Int) { "Value is not of Integer type" }
        return value
    }

    fun toBool(): Boolean {
        require(value is Boolean) { "Value is not of Boolean type" }
        return value
    }

    companion object {
        val NONE = Context(null)
        val ZERO = Context(0)

        fun ofInt(value: String) = Context(value.toInt())
        fun ofInt(value: Int) = Context(value)

        fun ofBool(value: String) = Context(value.toBoolean())
        fun ofBool(value: Boolean) = Context(value)
    }
}
