package aoc.y2025

import println
import readInput
import transpose

fun main() {
    fun part1(input: List<String>): Long {
        val homework = input.map { it.splitToSequence("\\s+".toRegex()).filter { entry -> entry.isNotBlank() }.toList() }
        var sum = 0L
        for (i in homework.last().indices) {
            when (homework.last()[i]) {
                "*" -> sum += homework.dropLast(1).fold(1L) { acc, c -> acc * c[i].toLong() }
                "+" -> sum += homework.dropLast(1).fold(0L) { acc, c -> acc + c[i].toLong() }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val homework = input.dropLast(1).map { line -> line.toList() }.transpose()
            .joinToString(",") { it.toCharArray().concatToString().trim() }.split(",,").map { it.split(",") }
        val operators = input.last().split("\\s+".toRegex()).filter { it.isNotBlank() }
        var sum = 0L
        for (i in homework.indices) {
            when (operators[i]) {
                "*" -> sum += homework[i].fold(1L) { acc, c -> acc * c.toLong() }
                "+" -> sum += homework[i].fold(0L) { acc, c -> acc + c.toLong() }
            }
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day06_test")
    check(part1(testInput) == 4277556L)
    check(part2(testInput) == 3263827L)

    val input = readInput(2025,"Day06")
    part1(input).println()
    part2(input).println()
}
