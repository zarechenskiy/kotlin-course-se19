package ru.hse.spb

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

internal class TexDslKtTest {

    private lateinit var out: ByteArrayOutputStream

    @BeforeEach
    fun initStream() {
        out = ByteArrayOutputStream()
    }

    @Test
    fun emptyDocument() {
//
//        document {}.toOutputStream(out)
//        assertEquals("\\begin{document}{")
    }
}