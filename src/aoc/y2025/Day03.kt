package aoc.y2025

import println
import readInput

fun main() {

    fun getLargestCharge(bank: String, digits: Int): Long {
        val digitList = mutableListOf<Pair<Char,Int>>()
        for (i in digits downTo 1) {
            val digit = bank.drop(digitList.lastOrNull()?.second ?: 0).dropLast(i-1).max()
            val index = bank.indexOf(digit, digitList.lastOrNull()?.second ?: 0)+1
            digitList.add(Pair(digit, index))
        }
        return digitList.map { it.first }.toCharArray().concatToString().toLong()
    }

    fun part1(input: List<String>): Long {
        val charges = input.map { getLargestCharge(it, 2) }
        return charges.sum()
    }

    fun part2(input: List<String>): Long {
        val charges = input.map { getLargestCharge(it, 12) }
        return charges.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day03_test")
    check(part1(testInput) == 357L)
    check(part2(testInput) == 3121910778619L)

    val input = readInput(2025,"Day03")
    part1(input).println()
    part2(input).println()
}
