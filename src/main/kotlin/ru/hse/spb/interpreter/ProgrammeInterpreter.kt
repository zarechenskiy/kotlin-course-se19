package ru.hse.spb.interpreter

import ru.hse.spb.parser.ProgrammeParser

class ProgrammeInterpreter(val parser: ProgrammeParser) {

    val programmeVisitor = ProgrammeVisitor()

    fun run(): Int {
        return parser.file().accept(programmeVisitor)
    }
}