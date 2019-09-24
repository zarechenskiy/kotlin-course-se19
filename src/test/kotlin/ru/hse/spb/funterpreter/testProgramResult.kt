package ru.hse.spb.funterpreter

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream


fun testProgramResult(program: String,
                        testOutput: Boolean = false, output: String? = null,
                        testReturned: Boolean = false, returned: Value? = null,
                        testValue: Boolean = false, value: Value? = null,
                        testException: Boolean = false, exception: FunException? = null) {
    val programOutput = ByteArrayOutputStream()
    System.setOut(PrintStream(programOutput))
    System.setErr(PrintStream(programOutput))

    val result = runProgram(program)

    if (testReturned) {
        assertEquals(returned, result.returned)
    }
    if (testOutput) {
        assertEquals(output, programOutput.toString())
    }
    if (testValue) {
        assertEquals(value, result.value)
    }
    if (testException) {
        assertEquals(result.exception, exception)
    }
}