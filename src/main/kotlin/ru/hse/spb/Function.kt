package ru.hse.spb

interface Function<ReturnType> {
    fun compute(state: State): ReturnType

    fun argsList() : List<String>
}