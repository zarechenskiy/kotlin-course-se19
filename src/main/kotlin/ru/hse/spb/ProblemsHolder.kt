package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext

class InterpretationException(line: Int, cause: Exception) : Exception("Got exception during interpretation in line $line.", cause)

inline fun <T> withContext(context: ParserRuleContext, body: () -> T): T {
    return try {
        body()
    } catch (e: MyLangVisitor.ReturnException) {
        throw e;
    } catch (e: Exception) {
        throw InterpretationException(context.start.line, e)
    }
}
