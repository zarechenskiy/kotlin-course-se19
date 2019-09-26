package ru.hse.spb

/**
 * Container for all types of scopes language supports.
 */
class Scopes {
    var variablesScope: Scope<Int?>
    var functionsScope: Scope<(List<Int?>) -> Int?>

    constructor() {
        variablesScope = Scope(null, "variable")
        functionsScope = Scope(null, "function")
    }

    constructor(parentScopes: Scopes) {
        variablesScope = Scope(parentScopes.variablesScope)
        functionsScope = Scope(parentScopes.functionsScope)
    }
}