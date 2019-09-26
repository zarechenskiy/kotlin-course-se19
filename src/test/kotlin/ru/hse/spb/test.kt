package ru.hse.spb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileWriter
import java.io.PrintStream
import java.util.*

class TestSource {

    fun runTestOnFile(content: String, expected: String) {
        val byteStream = ByteArrayOutputStream()
        val output = PrintStream(byteStream)

        System.setOut(output)

        val file = createTempFile()

        val writer = FileWriter(file)
        writer.write(content)

        writer.flush()

        main(arrayOf(file.absolutePath))

        output.flush()
        byteStream.flush()
        val scanner = Scanner(ByteArrayInputStream(byteStream.toByteArray()))
        val result = StringBuilder()

        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine())
        }

        assertEquals(expected, result.toString())
    }

    @Test
    fun testInput1() {
        val content = "var a = 10\n" +
                "var b = 20\n" +
                "if (a > b) {\n" +
                "    println(1)\n" +
                "} else {\n" +
                "    println(0)\n" +
                "}"

        val expected = "0"

        runTestOnFile(content, expected)
    }

    @Test
    fun testInput2() {
        val content = "fun fib(n) {\n" +
                "    if (n <= 1) {\n" +
                "        return 1\n" +
                "    }\n" +
                "    return fib(n - 1) + fib(n - 2)\n" +
                "}\n" +
                "\n" +
                "var i = 1\n" +
                "while (i <= 5) {\n" +
                "    println(i, fib(i))\n" +
                "    i = i + 1\n" +
                "}"

        val expected = "1 12 23 34 55 8"

        runTestOnFile(content, expected)
    }

    @Test
    fun testInput3() {
        val content = "fun foo(n) {\n" +
                "    fun bar(m) {\n" +
                "        return m + n\n" +
                "    }\n" +
                "\n" +
                "    return bar(1)\n" +
                "}\n" +
                "\n" +
                "println(foo(41)) // prints 42"

        val expected = "42"

        runTestOnFile(content, expected)
    }
}