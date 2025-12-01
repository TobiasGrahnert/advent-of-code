package aoc.y2023

import println
import readInput
import kotlin.math.pow

data class Card(val id: Int, val winningNumbers: Set<Int>, val numbers: Set<Int>) {
    private var count: Int = 1

    fun matchedNumbers(): Set<Int> = winningNumbers.intersect(numbers)

    fun points(): Int {
        if (matchedNumbers().isEmpty()) {
            return 0
        }
        return 2.0.pow(matchedNumbers().size-1).toInt()
    }

    fun copyCard(number: Int) {
        count += number
    }

    fun count(): Int = count
}

fun Card(line: String): Card {
    val numberRegex = """\d+""".toRegex()
    val colonIndex = line.indexOf(':')
    val barIndex = line.indexOf('|')
    var id = -1
    val winningNumbers = mutableSetOf<Int>()
    val numbers = mutableSetOf<Int>()
    for (match in numberRegex.findAll(line)) {
        if (match.range.first < colonIndex) {
            id = match.value.toInt()
        } else if (match.range.first < barIndex) {
            winningNumbers.add(match.value.toInt())
        } else {
            numbers.add(match.value.toInt())
        }
    }
    return Card(id, winningNumbers, numbers)
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { Card(it) }.sumOf { it.points() }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { Card(it) }
        for ((index, card) in cards.withIndex()) {
            for (matches in 1 .. card.matchedNumbers().size) {
                if (index + matches >= cards.size) break
                cards[index + matches].copyCard(card.count())
            }
        }
        return cards.sumOf { it.count() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2023,"Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput(2023,"Day04")
    part1(input).println()
    part2(input).println()
}
