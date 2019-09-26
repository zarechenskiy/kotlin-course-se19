fun foo(n) {
    fun bar(m) {
        return m + n
    }

    return bar(1)
}

println(foo(41)) // prints 42