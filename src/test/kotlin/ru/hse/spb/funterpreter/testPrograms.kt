package ru.hse.spb.funterpreter

import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class TestPrograms {

    @Test
    fun testPlus() {
        testProgramResult("2 + 3",
            testValue = true, value = IntValue(5)
        )
    }

    @Test
    fun syntaxError() {
        assertThrows(ParseCancellationException::class.java) {
            runProgram("var bob = (|||||:)")
        }
    }

    @Test
    fun `syntax error exception line is correct`() {
        try {
            runProgram("""
                var qwe
                fun f(x) {
                    return BOB!!!
                }
                f(0)
                this causes syntax error as well
            """.trimIndent())
        } catch (e: ParseCancellationException) {
            assertTrue(e.message!!.startsWith("line 3"))
            return
        }
        fail<Unit>("didn't find a syntax error")
    }

    @Test
    fun testOperations() {
        testProgramResult("4 - 2",
                testValue = true, value = IntValue(2)
        )
        testProgramResult("123 % 4",
                testValue = true, value = IntValue(3)
        )
        testProgramResult("1 && 1",
                testValue = true, value = IntValue(1)
        )
        testProgramResult("0 || 1",
                testValue = true, value = IntValue(1)
        )
    }

    @Test
    fun testReturn() {
        testProgramResult("return 5",
                testReturned = true, returned = IntValue(5)
        )
    }

    @Test
    fun testReturnExpression() {
        testProgramResult("return 5 * 5 + 1",
                testReturned = true, returned = IntValue(26)
        )
    }

    @Test
    fun `program stops on return`() {
        testProgramResult("return 5\nreturn 6",
                testReturned = true, returned = IntValue(5)
        )
    }

    @Test
    fun testFunction() {
        testProgramResult("""
            fun f(x, y) {
                return x + y            
            }
            return f(3, 5)
        """.trimIndent(),
                testReturned = true, returned = IntValue(8)
        )
    }

    @Test
    fun `function without parameters`() {
        testProgramResult("""
            fun f() {
                return 1
            }
            return f()
        """.trimIndent(),
                testReturned = true, returned = IntValue(1)
        )
    }

    @Test
    fun `assumes 0 as return value if no return`() {
        testProgramResult("""
            fun f() {
                1            
            }
            return f()
        """.trimIndent(),
                testReturned = true, returned = IntValue(0)
        )
    }

    @Test
    fun `variable assignment from function`() {
        testProgramResult("""
            var x = 1
            fun f() { 
                println(x) 
                x = x + 1
            }
            f()
            f()
            println(x)
        """.trimIndent(),
                testOutput = true, output = "1\n2\n3\n"
        )
    }

    @Test
    fun `shadowing not assignment`() {
        testProgramResult("""
            var x = 1
            fun newx() { 
                var x = x + 1
            }
            newx()
            return x
        """.trimIndent(),
                testReturned = true, returned = IntValue(1)
        )
    }

    @Test
    fun whileTest() {
        testProgramResult("""
            var i = 0
            var fb0 = 1
            var fb1 = 1
            while (i < 5) {
                var tmp = fb0 + fb1
                fb0 = fb1
                fb1 = tmp
                i = i + 1
            }
            return fb0
        """.trimIndent(),
                testReturned = true, returned = IntValue(8)
        )
    }

    @Test
    fun ifTrueTest() {
        testProgramResult("""
            if (1 == 1) { println(1) } else { println(2) }
        """.trimIndent(),
                testOutput = true, output = "1\n"
        )
    }

    @Test
    fun ifFalseTest() {
        testProgramResult("""
            if (1 != 1) { println(1) } else { println(2) }
        """.trimIndent(),
                testOutput = true, output = "2\n"
        )
    }

    @Test
    fun `return from deep block`() {
        testProgramResult("""
            var x = 1
            while (1) {
                x = 1
                x = 1
                while (1) {
                    if (1) {
                        return 123
                    } else {
                        return 123
                    }
                    return 2
                }
            }
        """.trimIndent(),
                testReturned = true, returned = IntValue(123)
        )
    }

    @Test
    fun `function variable`() {
        testProgramResult("""
            fun f() { println(1) }
            fun g() { println(2) }
            var x = f
            x()
            x = g
            x()
            f = x
            f()
        """.trimIndent(),
                testOutput = true, output = "1\n2\n2\n"
        )
    }

    @Test
    fun `comments are comments`() {
        testProgramResult("""
            var x = 0 x = 2 // x = 3 asdlj!!! (|||:) BOB!
            return x
        """.trimIndent(),
                testReturned = true, returned = IntValue(2)
        )
    }
}