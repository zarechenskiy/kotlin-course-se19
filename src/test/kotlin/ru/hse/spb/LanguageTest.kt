package ru.hse.spb

import org.junit.jupiter.api.BeforeEach
import java.io.ByteArrayOutputStream
import java.io.PrintStream

open class LanguageTest {
    protected lateinit var myOut: ByteArrayOutputStream

    @BeforeEach
    fun setOut() {
        myOut = ByteArrayOutputStream()
        System.setOut(PrintStream(myOut))
    }

    val lineBeaker = "\r\n"
}