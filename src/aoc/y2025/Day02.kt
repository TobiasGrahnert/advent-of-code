package aoc.y2025

import println
import readInput
import kotlin.math.pow

fun main() {
    val powers = (0..10).map { 10.0.pow(it).toLong()  }

    fun countDigits(number: Long): Int {
        var remainder = number
        var digits = 1
        for (i in intArrayOf(8,4,2,1)) {
            if (remainder >= powers[i]) {
                digits += i
                remainder /= powers[i]
            }
        }
        return digits
    }

    fun toRange(input: String): LongRange {
        val limits = input.split('-').map { it.toLong() }
        return LongRange(limits[0], limits[1])
    }

    fun findRepeating(repeats: MutableSet<Long>, range: LongRange, length: Int) {
        val front = range.first / powers[countDigits(range.last)-length]
        val end = range.last / powers[countDigits(range.last)-length]
        for (i in front..end) {
            var check = i
            while (i != 0L && check <= range.last) {
                if (range.contains(check)) {
                    repeats.add(check)
                }
                check = check*powers[countDigits(i)]+i
            }
        }
    }

    fun part1(input: List<String>): Long {
        val ranges = input[0].split(',')
            .map { toRange(it) }
        val invalidIds = mutableSetOf<Long>()
        for ( range in ranges ) {
            val front = range.first / powers[countDigits(range.last)/2]
            val end = range.last / powers[countDigits(range.last)/2]
            for (i in front..end) {
                if (range.contains(i*powers[countDigits(i)]+i)) {
                    invalidIds.add(i*powers[countDigits(i)]+i)
                }
            }
        }
        return invalidIds.sumOf { it }
    }

    fun part2(input: List<String>): Long {
        val ranges = input[0].split(',')
            .map { toRange(it) }
        val invalidIds = mutableSetOf<Long>()
        for ( range in ranges ) {
            for ( i in 1..(countDigits(range.last)+1)/2) {
                findRepeating(invalidIds, range, i)
            }
        }
        return invalidIds.sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2025,"Day02_test")
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)

    val input = readInput(2025,"Day02")
    part1(input).println()
    part2(input).println()
}
