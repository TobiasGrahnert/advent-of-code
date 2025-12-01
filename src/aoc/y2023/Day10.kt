package aoc.y2023

import println
import readInput
import java.security.InvalidParameterException

enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NONE
}
fun main() {

    fun Pair<Int,Int>.getAdjacent(direction: Direction): Pair<Int,Int> {
        return when (direction) {
            Direction.NORTH -> Pair(this.first-1, this.second)
            Direction.SOUTH -> Pair(this.first+1, this.second)
            Direction.WEST -> Pair(this.first, this.second-1)
            Direction.EAST -> Pair(this.first, this.second+1)
            Direction.NONE -> this
        }
    }

    fun Pair<Int,Int>.getDirectionTo(location: Pair<Int,Int>): Direction {
        if (this.first != location.first && this.second != location.second) throw InvalidParameterException()
        return when {
            this.first == location.first && this.second < location.second -> return Direction.EAST
            this.first == location.first && this.second > location.second -> return Direction.WEST
            this.second == location.second && this.first < location.first -> return Direction.SOUTH
            this.second == location.second && this.first > location.first -> return Direction.NORTH
            else -> Direction.NONE
        }
    }

    fun List<List<Char>>.connectedCells(currentLocation: Pair<Int,Int>): List<Pair<Int,Int>> {
        return when (this[currentLocation.first][currentLocation.second]) {
            '|' -> listOf(currentLocation.getAdjacent(Direction.NORTH),
                currentLocation.getAdjacent(Direction.SOUTH))
            '-' -> listOf(currentLocation.getAdjacent(Direction.WEST),
                currentLocation.getAdjacent(Direction.EAST))
            'L' -> listOf(currentLocation.getAdjacent(Direction.NORTH),
                currentLocation.getAdjacent(Direction.EAST))
            'J' -> listOf(currentLocation.getAdjacent(Direction.NORTH),
                currentLocation.getAdjacent(Direction.WEST))
            '7' -> listOf(currentLocation.getAdjacent(Direction.WEST),
                currentLocation.getAdjacent(Direction.SOUTH))
            'F' -> listOf(currentLocation.getAdjacent(Direction.EAST),
                currentLocation.getAdjacent(Direction.SOUTH))
            'S' -> listOf(currentLocation.getAdjacent(Direction.NORTH),
                currentLocation.getAdjacent(Direction.WEST),
                currentLocation.getAdjacent(Direction.EAST),
                currentLocation.getAdjacent(Direction.SOUTH))
                .filter { (it.first in indices) && (it.second in this[it.first].indices)}
                .filter { this.connectedCells(it).any { next -> next == currentLocation } }
            else -> emptyList()
        }.filter { (it.first in indices) && (it.second in this[it.first].indices)}
    }

    fun getLoop(
        start: Pair<Int, Int>,
        input: List<List<Char>>
    ): List<Pair<Int, Int>> {
        val loop = mutableListOf(start)
        do {
            val adjacentCells = input.connectedCells(loop.last()).filter { !loop.contains(it) }.take(1)
            loop.addAll(adjacentCells)
        } while (adjacentCells.isNotEmpty())
        return loop
    }

    fun part1(input: List<List<Char>>): Int {
        var start:Pair<Int, Int>? = null
        out@for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == 'S') {
                    start = Pair(y, x)
                    break@out
                }
            }
        }

        val loop = getLoop(start!!, input)
        return loop.size / 2
    }

    fun left(
        loop: List<Pair<Int, Int>>,
        currentTile: Pair<Int, Int>,
        input: List<List<Char>>
    ): List<Pair<Int, Int>> {
        val rowTiles = loop.filter { it.first == currentTile.first && it.second < currentTile.second }
        if (rowTiles.isEmpty()) return emptyList()
        val nextTile = rowTiles.maxWith(compareBy { it.second })
        return input[currentTile.first].withIndex()
            .filter { nextTile.second < it.index && it.index < currentTile.second }
            .map { Pair(currentTile.first, it.index) }
    }

    fun right(
        loop: List<Pair<Int, Int>>,
        currentTile: Pair<Int, Int>,
        input: List<List<Char>>
    ): List<Pair<Int, Int>> {
        val rowTiles = loop.filter { it.first == currentTile.first && it.second > currentTile.second }
        if (rowTiles.isEmpty()) return emptyList()
        val nextTile = rowTiles.minWith(compareBy { it.second })
        return input[currentTile.first].withIndex()
            .filter { currentTile.second < it.index && it.index < nextTile.second }
            .map { Pair(currentTile.first, it.index) }
    }

    fun part2(input: List<List<Char>>): Int {
        var start:Pair<Int, Int>? = null
        out@for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == 'S') {
                    start = Pair(y, x)
                    break@out
                }
            }
        }

        val loop = getLoop(start!!, input)

        val (startIndex, newStart) = loop.withIndex().minWith(compareBy<IndexedValue<Pair<Int, Int>>> { it.value.first }.thenBy { it.value.second })
        val counterClockwise = loop[startIndex+1].first > newStart.first || loop[startIndex+1].second < newStart.second
        val enclosedTiles = loop.indices.asSequence().flatMap {
            val idx = (it+startIndex)%loop.size
            val currentTile = loop[idx]
            val prevTile = loop[(idx + loop.size - 1) % loop.size]
            val adjacentTileDirections = setOf(currentTile.getDirectionTo(prevTile), currentTile.getDirectionTo(loop[(idx + loop.size + 1) % loop.size]))
            when (adjacentTileDirections) {
                setOf(Direction.NORTH, Direction.EAST) -> if (!counterClockwise && prevTile.first < currentTile.first
                    || counterClockwise && prevTile.second > currentTile.second)
                    left(loop, currentTile, input).asSequence()
                else emptySequence()

                setOf(Direction.SOUTH, Direction.WEST) -> if (!counterClockwise && prevTile.first > currentTile.first
                    || counterClockwise && prevTile.second < currentTile.second)
                    right(loop, currentTile, input).asSequence()
                else emptySequence()

                setOf(Direction.SOUTH, Direction.EAST) -> if (!counterClockwise && prevTile.second > currentTile.second
                    || counterClockwise && prevTile.first > currentTile.first)
                    left(loop, currentTile, input).asSequence()
                else emptySequence()

                setOf(Direction.NORTH, Direction.WEST) -> if (!counterClockwise && prevTile.second < currentTile.second
                    || counterClockwise && prevTile.first < currentTile.first)
                    right(loop, currentTile, input).asSequence()
                else emptySequence()

                setOf(Direction.NORTH, Direction.SOUTH) -> {
                    if (!counterClockwise == prevTile.first < currentTile.first)
                        left(loop, currentTile, input).asSequence()
                    else if (!counterClockwise == prevTile.first > currentTile.first)
                        right(loop, currentTile, input).asSequence()
                    else emptySequence()
                }

                else -> emptySequence()
            }
        }.toSet()

        return enclosedTiles.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput(2023,"Day10_test").map { it.toList() }
    check(part1(testInput1) == 8)
    val testInput2 = readInput(2023,"Day10_test2").map { it.toList() }
    check(part2(testInput2) == 8)
    val testInput3 = readInput(2023,"Day10_test3").map { it.toList() }
    check(part2(testInput3) == 10)

    val input = readInput(2023,"Day10").map { it.toList() }
    part1(input).println()
    part2(input).println()
}
