package ru.hse.spb

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

private data class Station(
    val id: Int,
    val adjacentStations: MutableList<Station>,
    var visited: Boolean = false,
    var distanceToCircle: Int? = null
)

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
    station.adjacentStations.forEach {
        if (it != stationFrom) {
            if (!it.visited) {
                if (dfs(it, station, path)) {
                    return true
                }
            } else {
                path.addLast(it)
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

private fun computeDistancesToCircle(circle: List<Station>) {
    val bfsQueue = ArrayDeque<Station>()
    bfsQueue += circle
    circle.forEach { it.distanceToCircle = 0 }

    while (bfsQueue.isNotEmpty()) {
        val currentStation = bfsQueue.pop()
        currentStation.adjacentStations.forEach {
            if (it.distanceToCircle == null) {
                it.distanceToCircle = currentStation.distanceToCircle!! + 1
                bfsQueue += it
            }
        }
    }
}

private fun outputResult(stations: Array<Station>, outputStream: OutputStream) {
    val writer = PrintWriter(outputStream)
    writer.println(
        stations.map { it.distanceToCircle!! }.joinToString(separator = " ")
    )
    writer.close()
}

fun executeSolution(inputStream: InputStream, outputStream: OutputStream) {
    val stations = readDataset(inputStream)
    computeDistancesToCircle(findCircle(stations[0]))
    outputResult(stations, outputStream)
}

fun main() {
    executeSolution(System.`in`, System.out)
}