package aoc.y2025

import println
import readInput

fun main() {
    fun part1(input: List<String>): Long {
        val indexes = mutableSetOf(input[0].indexOf('S'))
        var sum = 0L
        val indexRange = 0..<input[0].length
        for (row in input.drop(1)) {
            for (i in indexes.toSet()) {
                if (row[i] == '^') {
                    indexes -= i
                    if (indexRange.contains(i-1)) {
                        indexes += i-1
                    }
                    if (indexRange.contains(i+1)) {
                        indexes += i+1
                    }
                    sum++
                }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val indexes = hashMapOf(input[0].indexOf('S') to 1L)
        val indexRange = 0..<input[0].length
        for (row in input.drop(1)) {
            for (i in indexes.toMap()) {
                if (row[i.key] == '^') {
                    indexes.remove(i.key)
                    if (indexRange.contains(i.key - 1)) {
                        indexes.compute(i.key - 1) { _, v -> (v ?: 0L) + i.value }
                    }
                    if (indexRange.contains(i.key + 1)) {
                        indexes.compute(i.key + 1) { _, v -> (v ?: 0L) + i.value }
                    }
                }
            }
        }
        return indexes.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day07_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 40L)

    val input = readInput(2025,"Day07")
    part1(input).println()
    part2(input).println()
}
