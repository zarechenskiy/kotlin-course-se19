package ru.hse.spb


fun main(args: Array<String>) {

    if (args.isEmpty()) {
        error("first argument is missing")
    }

    Interpreter.run(args[0])
}