package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SubwayGraph {
    private enum class DfsTraverseType {
        NOT_VISITED, IN_PROGRESS, FINISHED
    }

    private data class Station(val id: Int,
                               var traverseType: DfsTraverseType = DfsTraverseType.NOT_VISITED,
                               var onCycle: Boolean = false,
                               var parentIdOnTraversePath: Station? = null)

    private val stations: MutableMap<Int, Station> = HashMap()
    private val adjacentStations: MutableMap<Int, MutableList<Station>> = HashMap()

    fun addStationIfNotExist(vararg stationIds: Int) {
        stationIds.forEach { id -> stations.putIfAbsent(id, Station(id)) }
    }

    fun addNotDirectedTransition(firstStationId: Int, secondStationId: Int) {
        addDirectedTransitions(firstStationId, secondStationId)
        addDirectedTransitions(secondStationId, firstStationId)
    }

    fun addDirectedTransitions(fromId: Int, toId: Int) {
        addStationIfNotExist(fromId, toId)

        val toStation = stations[toId]
        adjacentStations.putIfAbsent(fromId, ArrayList())
        adjacentStations[fromId]?.add(toStation!!)
    }

    fun getDistancesFromRingStations(): Map<Int, Int> {
        val distancesFromRing = HashMap<Int, Int>()
        markRingStationsDfs(stations.values.first())
        stations.values
                .filter { station -> station.onCycle }
                .forEach { station -> distancesFromRing.putAll(getDistancesFromRingStation(station)) }
        return distancesFromRing
    }

    private fun getDistancesFromRingStation(currentStation: Station,
                                            previousStation: Station? = null,
                                            currentDistance: Int = 0): Map<Int, Int> {
        val distances = HashMap<Int, Int>()
        distances[currentStation.id] = currentDistance

        getAdjacentStations(currentStation.id)
                .filter { adjacentStation -> adjacentStation.id != previousStation?.id }
                .filterNot { adjacentStation -> adjacentStation.onCycle}
                .forEach { adjacentStation ->
                    distances.putAll(getDistancesFromRingStation(adjacentStation,
                            currentStation, currentDistance + 1)) }
        return distances
    }

    private fun getAdjacentStations(vertexId: Int): List<Station> {
        if (!adjacentStations.containsKey(vertexId)) {
            throw RuntimeException("Vertex with id=$vertexId is not presented in the graph.")
        }
        return adjacentStations[vertexId]!!
    }

    private fun markRingStationsDfs(currentStation: Station): Boolean {
        currentStation.traverseType = DfsTraverseType.IN_PROGRESS

        getAdjacentStations(currentStation.id)
                .filter { adjacentStation -> currentStation.parentIdOnTraversePath?.id != adjacentStation.id }
                .forEach { adjacentStation ->
                    when (adjacentStation.traverseType) {
                        DfsTraverseType.NOT_VISITED -> {
                            adjacentStation.parentIdOnTraversePath = currentStation
                            if (markRingStationsDfs(adjacentStation)) {
                                return true
                            }
                        }

                        DfsTraverseType.IN_PROGRESS -> {
                            markRingStations(currentStation, adjacentStation)
                            return true
                        }

                        DfsTraverseType.FINISHED -> return false
                    }
                }

        currentStation.traverseType = DfsTraverseType.FINISHED
        return false
    }

    private fun markRingStations(startStation: Station, endStation: Station) {
        var currentVertex = startStation
        while (currentVertex != endStation) {
            currentVertex.onCycle = true
            currentVertex = currentVertex.parentIdOnTraversePath!!
        }
        endStation.onCycle = true
    }
}


fun readSubwayGraph(): SubwayGraph {
    val scanner = Scanner(System.`in`)

    val graph = SubwayGraph()

    val transitionsCount = scanner.nextInt()
    for (i in 1..transitionsCount) {
        graph.addNotDirectedTransition(scanner.nextInt(), scanner.nextInt())
    }

    return graph
}

fun printDistances(distances: Map<Int, Int>) {
    for (i in 1..distances.size) {
        print("${distances[i]} ")
    }
}

fun main() {
    val graph = readSubwayGraph()
    printDistances(graph.getDistancesFromRingStations())
}
