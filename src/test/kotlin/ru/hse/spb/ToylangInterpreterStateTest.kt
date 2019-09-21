package ru.hse.spb

import org.junit.Assert
import org.junit.Test


class ToylangInterpreterStateTest {

    private inline fun <reified A, reified B> withState(
        builtIn: List<Pair<String, B>> = emptyList(),
        block: (InterpreterState<A, B>) -> Unit
    ) {
        val state = ToylangInterpreterState<A, B>()
        builtIn.forEach { state.putFunctionInLastFrame(it.first, it.second) }
        block(state)
    }

    @Test
    fun testReturnValue() {
        withState<IntegralType, FunctionType> {
            Assert.assertEquals(null, it.getReturnValue())
            Assert.assertEquals(false, it.setReturnValue(0))

            Assert.assertEquals(true, it.addFrame(true))
            Assert.assertEquals(true, it.setReturnValue(1))
            Assert.assertEquals(1, it.getReturnValue())
            Assert.assertEquals(true, it.addFrame())
            Assert.assertEquals(1, it.getReturnValue())


            Assert.assertEquals(true, it.popFrame())
            Assert.assertEquals(1, it.getReturnValue())
            Assert.assertEquals(true, it.popFrame())
            Assert.assertEquals(null, it.getReturnValue())
        }
    }

    @Test
    fun testFrameManaging() {
        withState<IntegralType, FunctionType> {
            val st = it
            val i = 100
            repeat(i) {
                Assert.assertEquals(true, st.addFrame())
            }
            repeat(i) {
                Assert.assertEquals(true, st.popFrame())
            }
        }
    }


    @Test
    fun testLookup() {
        withState<IntegralType, FunctionType> {
            it.putVariableInLastFrame("frame0", 0)

            it.addFrame()
            it.putVariableInLastFrame("frame1", 1)

            Assert.assertEquals(0, it.lookupVariable("frame0"))
            Assert.assertEquals(null, it.lookupVariableInLastFrame("frame0"))

            Assert.assertEquals(1, it.lookupVariable("frame1"))
            Assert.assertEquals(1, it.lookupVariableInLastFrame("frame1"))

            it.popFrame()

            Assert.assertEquals(0, it.lookupVariable("frame0"))
            Assert.assertEquals(0, it.lookupVariableInLastFrame("frame0"))

            Assert.assertEquals(null, it.lookupVariable("frame1"))
            Assert.assertEquals(null, it.lookupVariableInLastFrame("frame1"))
        }
    }
}