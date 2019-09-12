import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException
import java.util.*

class Metro(numberOfStations: Int) {
    private val stations: Array<Station>
    private var tunnelsLeft: Int

    init {
        stations = Array(numberOfStations + 1) { Station() }
        tunnelsLeft = numberOfStations
    }

    class Station {
        var neighborStations = arrayListOf<Station>()
        var visited = false
        var isOnCircleRoute = false
        var distanceFromCircleRoute = -1

        fun markCircleRoute(parent: Station, path: Stack<Station>): Boolean {
            path.push(this)
            if (visited) {
                do {
                    path.pop().isOnCircleRoute = true
                } while (path.peek() != this)
                return true
            }

            visited = true
            for (neighbour in neighborStations) {
                if (neighbour != parent && neighbour.markCircleRoute(this, path)) {
                    return true
                }
            }
            path.pop()
            return false
        }

        fun calculateDistancesFromCircleRoute(distance: Int = 0) {
            if (distanceFromCircleRoute != -1) {
                return
            }
            distanceFromCircleRoute = if (isOnCircleRoute) 0 else distance
            for (neighbour in neighborStations) {
                neighbour.calculateDistancesFromCircleRoute(distanceFromCircleRoute + 1)
            }
        }
    }

    private fun checkIndex(index: Int) {
        if (index <= 0 || index > stations.size) {
            throw IndexOutOfBoundsException("Station index must be in range [1, numberOfStations]")
        }
    }

    fun addTunnel(firstStationIndex: Int, secondStationIndex: Int): Metro {
        if (tunnelsLeft == 0) {
            throw IllegalStateException("Number of tunnels already exceeded number of stations");
        }
        tunnelsLeft--
        checkIndex(firstStationIndex)
        checkIndex(secondStationIndex)

        val firstStation = stations[firstStationIndex]
        val secondStation = stations[secondStationIndex]
        firstStation.neighborStations.add(secondStation)
        secondStation.neighborStations.add(firstStation)
        return this
    }

    fun calculateDistancesFromCircleRoute() {
        if (tunnelsLeft > 0) {
            throw IllegalStateException("Number of tunnels is less than number of stations")
        }

        val path = Stack<Station>()
        stations.last().markCircleRoute(stations.first(), path)
        for (station in stations) {
            if (station.isOnCircleRoute) {
                station.calculateDistancesFromCircleRoute()
                break
            }
        }
    }

    fun getDistanceFromCircleRoute(stationIndex: Int) = stations[stationIndex].distanceFromCircleRoute
}

fun main() {
    with(Scanner(System.`in`)) {
        val numberOfStations = nextInt()
        val metro = Metro(numberOfStations)
        for (i in 1..numberOfStations) {
            metro.addTunnel(nextInt(), nextInt())
        }
        metro.calculateDistancesFromCircleRoute()
        for (i in 1..numberOfStations) {
            print(metro.getDistanceFromCircleRoute(i))
            print(' ')
        }
    }
}