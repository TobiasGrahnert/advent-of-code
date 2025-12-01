package aoc.y2023

import println
import readInput

fun main() {
    fun getWaysToWin(times: List<Long>, distances: List<Long>):List<Long> {
        val results = mutableListOf<Long>()
        for ( (index, time) in times.withIndex()) {
            var count = 0L
            for (i in 1..<time) {
                if ((time - i) * i > distances[index]) count++
                else if (count > 0) break
            }
            if (count > 0) results.addLast(count)
        }
        return results
    }

    fun part1(input: List<String>): Long {
        val times = input[0].split("""\s""".toRegex()).filter { it.isNotBlank() }.drop(1).map { it.toLong() }
        val distances = input[1].split("""\s""".toRegex()).filter { it.isNotBlank() }.drop(1).map { it.toLong() }
        val waysToWin = getWaysToWin(times, distances)
        return waysToWin.reduce {acc, i -> acc*i }
    }

    fun part2(input: List<String>): Long {
        val times = input[0].split("""\s""".toRegex(), 2).drop(1).map { it.replace("""\s""".toRegex(), "").toLong() }
        val distances = input[1].split("""\s""".toRegex(), 2).drop(1).map { it.replace("""\s""".toRegex(), "").toLong() }
        val waysToWin = getWaysToWin(times, distances)
        return waysToWin.reduce {acc, i -> acc*i }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2023,"Day06_test")
    check(part1(testInput) == 288L)
    check(part2(testInput) == 71503L)

    val input = readInput(2023,"Day06")
    part1(input).println()
    part2(input).println()
}
