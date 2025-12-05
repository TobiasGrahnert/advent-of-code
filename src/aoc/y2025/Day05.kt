package aoc.y2025

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val ranges = input.filter { it.contains("-") }.map { LongRange(it.substringBefore("-").toLong(), it.substringAfter("-").toLong()) }
        val ingredientIds = input.filter { !it.contains("-") && it.isNotBlank() }.map { it.toLong() }
        return ingredientIds.filter { ingredientId -> ranges.any { range -> range.contains(ingredientId) } }.size
    }

    fun part2(input: List<String>): Long {
        val ranges = input.filter { it.contains("-") }.map { LongRange(it.substringBefore("-").toLong(), it.substringAfter("-").toLong()) }.sortedBy { it.first }
        var numberOfIds = 0L
        var maxRange = -1L
        for (range in ranges) {
            if (range.contains(maxRange)) {
                numberOfIds += range.last - maxRange
                maxRange = range.last
            } else if (range.first > maxRange) {
                numberOfIds += range.last - range.first + 1
                maxRange = range.last
            }
        }
        return numberOfIds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day05_test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 14L)

    val input = readInput(2025,"Day05")
    part1(input).println()
    part2(input).println()
}
