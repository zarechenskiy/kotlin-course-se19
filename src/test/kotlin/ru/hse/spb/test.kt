package ru.hse.spb

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Scanner

class TestSource {
    @Test
    fun testFirst() {
        assertTrue(belongsToLanguage(Scanner("petr")))
    }

    @Test
    fun testSecond() {
        assertFalse(belongsToLanguage(Scanner("etis atis animatis etis atis amatis")))
    }

    @Test
    fun testThird() {
        assertTrue(belongsToLanguage(Scanner("nataliala kataliala vetra feinites")))
    }
}