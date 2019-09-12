package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalStateException

class TestMetro {
    @Test
    fun testOneCircle() {
        val metro = Metro(3).addTunnel(1, 2).addTunnel(1, 3).addTunnel(3, 2)
        metro.calculateDistancesFromCircleRoute()
        assertEquals(0, metro.getDistanceFromCircleRoute(1))
        assertEquals(0, metro.getDistanceFromCircleRoute(2))
        assertEquals(0, metro.getDistanceFromCircleRoute(3))
    }

    @Test
    fun testComplex() {
        val metro = Metro(10)
                .addTunnel(6, 1).addTunnel(5, 4).addTunnel(2, 3).addTunnel(2, 9).addTunnel(7, 2)
                .addTunnel(2, 6).addTunnel(1, 7).addTunnel(7, 5).addTunnel(4, 8).addTunnel(4, 10)
        metro.calculateDistancesFromCircleRoute()
        assertEquals(0, metro.getDistanceFromCircleRoute(1))
        assertEquals(0, metro.getDistanceFromCircleRoute(2))
        assertEquals(0, metro.getDistanceFromCircleRoute(6))
        assertEquals(0, metro.getDistanceFromCircleRoute(7))
        assertEquals(1, metro.getDistanceFromCircleRoute(3))
        assertEquals(1, metro.getDistanceFromCircleRoute(9))
        assertEquals(1, metro.getDistanceFromCircleRoute(5))
        assertEquals(2, metro.getDistanceFromCircleRoute(4))
        assertEquals(3, metro.getDistanceFromCircleRoute(8))
        assertEquals(3, metro.getDistanceFromCircleRoute(10))
    }

    @Test(expected = IllegalStateException::class)
    fun testTooManyTunnels() {
        Metro(4).addTunnel(1, 2).addTunnel(1, 3).addTunnel(3, 2).addTunnel(1, 4).addTunnel(4, 2)
    }

    @Test(expected = IllegalStateException::class)
    fun testNotEnoughTunnels() {
        val metro = Metro(4).addTunnel(1, 2).addTunnel(1, 3).addTunnel(3, 2)
        metro.calculateDistancesFromCircleRoute()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testZeroIndex() {
        Metro(4).addTunnel(0, 2)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testTooLargeIndex() {
        Metro(4).addTunnel(1, 5)
    }
}