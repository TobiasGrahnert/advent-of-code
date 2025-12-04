package aoc.y2025

import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    val roll = '@'
    val empty = '.'

    fun checkSurrounding(map: List<List<Char>>, y: Int, x: Int): Int {
        return map.map { row -> row.subList(max(x-1,0), min(x+2, row.size)).count { it == roll } }
            .subList(max(y-1, 0), min(y+2, map.size)).sum() - 1
    }

    fun checkMap(map: List<List<Char>>): Sequence<Pair<Int,Int>> {
        return sequence {
            map.forEachIndexed { y, row ->
                row.forEachIndexed { x, cell ->
                    if (cell == roll
                        && checkSurrounding(map, y, x) < 4) {
                        yield(Pair(y, x))
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val map = input.map { it.toList() }
        return checkMap(map).count()
    }

    fun part2(input: List<String>): Int {
        val map = input.map { it.toMutableList() }.toMutableList()
        var removedRolls = 0
        while (true) {
            checkMap(map).apply {
                forEach {
                    map[it.first][it.second] = empty
                    removedRolls++
                }
                if (none()) {
                    return removedRolls
                }
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 43)

    val input = readInput(2025,"Day04")
    part1(input).println()
    part2(input).println()
}
