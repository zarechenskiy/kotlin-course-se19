package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InterpreterTest : LanguageTest() {


    @Test
    fun runFullCommentedProgram() {
        val program = """
            |//println(1)
            |//if (1) {
            |   //println(1)
            |//}
            |//
            |//while(1) {
            |//   println(1)
            |//}
            """.trimMargin()

        Interpreter.run(program)
        assertEquals("", myOut.toString())
    }

    @Test
    fun runProgramWithComments() {
        val program = """
                fun foo(n) {// fun foo
                    
                    
                    fun bar(m) { //fun bar
                        
                        // return m // skip
                        
                        return m + n// add m to n an return
                        
                    }
                    
                    return bar(1) // add 1 to n and return
                }
                // lol
                                        println(foo(41)) // prints 42
                // kek
                
            """.trimIndent()

        Interpreter.run(program)
        assertEquals("42$lineBeaker", myOut.toString())
    }
}