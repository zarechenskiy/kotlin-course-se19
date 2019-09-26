fun fib(n) {
    if (n <= 1) {
        return 1
    }
    return fib(n - 1) + fib(n - 2)
}

var i = 1
while (i <= 5) {
    println(i, fib(i))
    i = i + 1
}