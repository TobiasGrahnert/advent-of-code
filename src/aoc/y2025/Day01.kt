package aoc.y2025

import println
import readInput

fun main() {

    fun part1(input: List<String>): Int {
        var zeroCount = 0
        val start = 50
        val size = 100
        var dialPosition = start
        for (line in input) {
            val direction = line.first()
            val steps = line.drop(1).toInt()
            dialPosition += (if ('L' == direction) -1*steps else steps)
            dialPosition %= size
            if (dialPosition == 0) {
                zeroCount++
            }
        }
        return zeroCount
    }

    fun part2(input: List<String>): Int {
        val size = 100
        val ring = (0..<size).toList()
        var index = 50
        var zeroCount = 0
        for (line in input) {
            val direction = line.first()
            var steps = line.drop(1).toInt()
            while (steps>0) {
                index += (if ('L' == direction) -1 else 1)
                index += size
                index %= size
                steps--
                if (ring[index] == 0) {
                    zeroCount++
                }
            }
        }
        return zeroCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day01_test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 6)

    val input = readInput(2025,"Day01")
    part1(input).println()
    part2(input).println()
}
