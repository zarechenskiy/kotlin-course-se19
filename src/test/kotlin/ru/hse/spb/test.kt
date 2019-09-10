package ru.hse.spb

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

public class TestSource {

    private fun test(fileName: String) {
        val programOutput = ByteArrayOutputStream()
        solveTask(FileInputStream("src/test/testData/${fileName}.txt"), programOutput)
        val answerReader = Files.newBufferedReader(Paths.get("src/test/testData/${fileName}-answer.txt"))
        val answer1 = answerReader.readLine()
        val answer2 = answerReader.readLine()
        val answerProgram = programOutput.toString()
        assertTrue(answer1 == answerProgram || answer2 == answerProgram)
    }

    @Test
    fun test1() {
        test("test1")
    }
    @Test
    fun test2() {
        test("test2")
    }
}