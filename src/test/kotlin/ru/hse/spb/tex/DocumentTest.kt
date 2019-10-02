package ru.hse.spb.tex

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DocumentTest {

    lateinit var subject: Document

    fun test(expectedPrelude: String, expectedBody: String = "", init: Document.() -> Unit) {
        val expected = "$expectedPrelude\n\\begin{document}\n$expectedBody\\end{document}\n"
        test(expected, subject, init)
    }

    @BeforeEach
    fun init() {
        subject = Document()
    }

    @Test
    fun documentClassTest() {
        test("""
            \documentclass{kek}
            
        """.trimIndent()) {
            documentClass("kek")
        }
    }

    @Test
    fun documentClassArgumentsTest() {
        test("""
            \documentclass[12pt]{article}
            
        """.trimIndent()) {
            documentClass["12pt"]("article")
        }
    }

    @Test
    fun `default documentClass`() {
        test("""
            \documentclass{article}
            
        """.trimIndent()) {}
    }

    @Test
    fun `multiple documentClass throws`() {
        assertThrows<Document.MultipleDocumentClassException> {
            document { documentClass; documentClass }
        }
    }

    @Test
    fun `init command test`() {
        test("""
            \documentclass{article}
            
            \bob
        """.trimIndent()) {
            initCommand("bob")
        }
    }

    @Test
    fun `def test`() {
        test("""
            \documentclass{article}
            
            \def\bob{(|||||:)}
        """.trimIndent()) {
            def("bob")("(|||||:)")
        }
    }

    @Test
    fun `newcommand test`() {
        test("""
            \documentclass{article}
            
            \newcommand{\frob}[1]{||#1||_F}
        """.trimIndent()) {
            newcommand("frob")["1"]("||#1||_F")
        }
    }

    @Test
    fun `newcommand with body test`() {
        test("""
            \documentclass{article}
            
            \newcommand{\aaaa}{
                qwe
                \www
            }
        """.trimIndent()) {
            newcommand("aaaa") {
                +"qwe"
                command("www")
            }
        }
    }

    @Test
    fun `usepackage full parameter test`() {
        test("""
            \documentclass{article}
            
            \usepackage[utf8]{inputenc}
        """.trimIndent()) {
            usepackage["utf8"]("inputenc")
        }
    }

    @Test
    fun `multiple usepackages`() {
        test("""
            \documentclass{article}
            
            \usepackage[utf8]{inputenc}
            \usepackage[utf8]{inputenc}
        """.trimIndent()) {
            usepackage["utf8"]("inputenc")
            usepackage["utf8"]("inputenc")
        }
    }

    @Test
    fun `frame lazy test`() {
        test(
            expectedPrelude = "\\documentclass{article}\n",
            expectedBody = """
                \begin{frame}
                    123
                    
                    311
                \end{frame}
                
        """.trimIndent()) {
            frame {
                +"123"
                emptyLn()
                +"311"
            }
        }
    }
}