package ru.hse.spb

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