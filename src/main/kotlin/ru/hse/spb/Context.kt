package ru.hse.spb

data class Context(val value: Any?) {
    fun toInt(): Int {
        require(value is Int) { "Value is not of Integer type" }
        return value as Int
    }

    fun toBool(): Boolean {
        require(value is Boolean) { "Value is not of Boolean type" }
        return value as Boolean
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
