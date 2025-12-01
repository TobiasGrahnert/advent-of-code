package aoc.y2023

import println
import readInput
enum class WinningHand() {
    FIVE,
    FOUR,
    FULL_HOUSE,
    THREE,
    TWO_PAIR,
    PAIR,
    HIGH
}

data class Hand(val cards: String, val bet: Int, val faces: Set<Char>, val joker: Char) : Comparable<Hand> {
    private fun getBestHand(): WinningHand {
        val groups = cards.groupBy { it }
        val jokers = groups[joker]?: emptyList()
        if (groups.any { it.value.size + (if (it.key != joker) jokers.size else 0) == 5 }) return WinningHand.FIVE
        if (groups.any { it.value.size + (if (it.key != joker) jokers.size else 0) == 4 }) return WinningHand.FOUR
        if (groups.any { it.value.size == 3 }) {
            if (groups.any { it.value.size == 2 }) return WinningHand.FULL_HOUSE
            return WinningHand.THREE
        }
        val pairs = groups.count { it.value.size == 2 }
        if (groups.any { it.value.size + (if (it.key != joker) jokers.size else 0) == 3 }) {
            if (pairs == 2) return WinningHand.FULL_HOUSE
            return WinningHand.THREE
        }
        if (pairs == 2) return WinningHand.TWO_PAIR
        if (pairs == 1 || jokers.size == 1) return WinningHand.PAIR
        return WinningHand.HIGH
    }

    private fun compareCards(cards1: String, cards2: String): Int {
        for ((index, card) in cards1.withIndex()) {
            if (card == cards2[index])
                continue
            return faces.indexOf(card) compareTo faces.indexOf(cards2[index])
        }
        return 0
    }

    override fun compareTo(other: Hand): Int = when {
        this.getBestHand() != other.getBestHand() -> this.getBestHand() compareTo other.getBestHand()
        else -> compareCards(this.cards, other.cards)
    }
}
fun main() {


    fun part1(input: List<String>): Int {
        val faces = setOf('A','K','Q','J','T','9','8','7','6','5','4','3','2')
        val hands = input.map { it.split(" ") }
            .map { Hand(it[0], it[1].toInt(), faces, '0') }.sortedDescending()
        val values = hands.mapIndexed { index: Int, hand: Hand -> (index +1)*hand.bet }
        return values.sum()
    }

    fun part2(input: List<String>): Int {
        val faces = setOf('A','K','Q','T','9','8','7','6','5','4','3','2','J')
        val hands = input.map { it.split(" ") }
            .map { Hand(it[0], it[1].toInt(), faces, 'J') }.sortedDescending()
        val values = hands.mapIndexed { index: Int, hand: Hand -> (index +1)*hand.bet }
        return values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2023,"Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput(2023,"Day07")
    part1(input).println()
    part2(input).println()
}
