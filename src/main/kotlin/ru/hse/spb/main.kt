package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

private data class Station(
    val id: Int,
    val adjacentStations: MutableList<Station>
) {
    var visited: Boolean = false
}

private fun readDataset(inputStream: InputStream): Array<Station> {
    val scanner = Scanner(inputStream)

    val stationCount = scanner.nextInt()
    val stations = Array(stationCount) {
        Station(it, mutableListOf())
    }

    repeat(stationCount) {
        val from = scanner.nextInt() - 1
        val to = scanner.nextInt() - 1
        stations[from].adjacentStations.add(stations[to])
        stations[to].adjacentStations.add(stations[from])
    }

    scanner.close()
    return stations
}

private fun dfs(station: Station, stationFrom: Station?, path: ArrayDeque<Station>): Boolean {
    path.addLast(station)
    station.visited = true
    for (stationNext in station.adjacentStations) {
        if (stationNext != stationFrom) {
            if (!stationNext.visited) {
                if (dfs(stationNext, station, path)) {
                    return true
                }
            } else {
                path.addLast(stationNext)
                return true
            }
        }
    }
    path.removeLast()
    return false
}

private fun findCircle(station: Station): List<Station> {
    val path = ArrayDeque<Station>()
    dfs(station, null, path)
    return path.dropWhile { it.id != path.last.id }
}

private fun computeDistancesToCircle(circle: List<Station>, stationCount: Int): Array<Int> {
    val distancesToCircle = Array<Int?>(stationCount) { null }

    val bfsQueue = ArrayDeque<Station>()
    bfsQueue += circle
    for (station in circle) {
        distancesToCircle[station.id] = 0
    }

    while (bfsQueue.isNotEmpty()) {
        val currentStation = bfsQueue.pop()
        for (nextStation in currentStation.adjacentStations) {
            if (distancesToCircle[nextStation.id] == null) {
                distancesToCircle[nextStation.id] = distancesToCircle[currentStation.id]!! + 1
                bfsQueue += nextStation
            }
        }
    }

    return distancesToCircle.requireNoNulls()
}

private fun outputResult(distancesToCircle: Array<Int>, outputStream: OutputStream) {
    val writer = PrintWriter(outputStream)
    writer.println(distancesToCircle.joinToString(separator = " "))
    writer.close()
}

fun executeSolution(inputStream: InputStream, outputStream: OutputStream) {
    val stations = readDataset(inputStream)
    val distancesToCircle = computeDistancesToCircle(findCircle(stations[0]), stations.size)
    outputResult(distancesToCircle, outputStream)
}

fun main() {
    executeSolution(System.`in`, System.out)
}