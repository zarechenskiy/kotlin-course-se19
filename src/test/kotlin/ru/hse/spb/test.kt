package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testAllStationOnRing() {
        val graph = SubwayGraph()
        graph.addNotDirectedTransition(1, 3)
        graph.addNotDirectedTransition(3, 4)
        graph.addNotDirectedTransition(4, 2)
        graph.addNotDirectedTransition(2, 1)

        val distances = graph.getDistancesFromRingStations()

        assertEquals(4, distances.size)
        for (id in 1..4) {
            assertEquals(0, distances[id])
        }
    }

    @Test
    fun testRingWithTwoBranchesFromOneStation() {
        val graph = SubwayGraph()
        graph.addNotDirectedTransition(1, 2)
        graph.addNotDirectedTransition(2, 3)
        graph.addNotDirectedTransition(3, 1)
        graph.addNotDirectedTransition(3, 5)
        graph.addNotDirectedTransition(3, 4)
        graph.addNotDirectedTransition(4, 6)

        val distances = graph.getDistancesFromRingStations()

        assertEquals(6, distances.size)
        for (id in 1..3)
            assertEquals(0, distances[id])
        for (id in 4..5) {
            assertEquals(1, distances[id])
        }
        assertEquals(2, distances[6])
    }
}