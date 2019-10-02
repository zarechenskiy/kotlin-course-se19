package ru.hse.spb.tex

import ru.hse.spb.tex.util.pairsToParameter

open class CommandInitializer<Y : Element, T : CommandWithBody<Y>>(val command: T) {

    operator fun get(vararg params: String): CommandInitializer<Y, T> {
        command.addSquareArguments(*params)
        return this
    }
    operator fun get(param: Pair<String, String>, vararg params: Pair<String, String>): CommandInitializer<Y, T> =
        get(pairsToParameter(param, *params))

    operator fun invoke(vararg figureParams: String): CommandInitializer<Y, T> {
        command.addFigureArguments(*figureParams)
        return this
    }

    operator fun invoke(commandInit: Y.() -> Unit) {
        command.initBody(commandInit)
    }

    operator fun invoke(vararg figureParams: String, commandInit: Y.() -> Unit) {
        invoke(*figureParams)
        invoke(commandInit)
    }
}

open class CommandWithoutBodyInitializer(
    commandName: String,
    commandConsumer: (Command) -> Any
)
    : CommandInitializer<Command.EmptyElement, Command>(
    Command(commandName).also { commandConsumer(it) }
)