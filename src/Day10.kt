enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST
}
fun main() {

    fun Pair<Int,Int>.getAdjacent(direction: Direction): Pair<Int,Int> {
        return when (direction) {
            Direction.NORTH -> Pair(this.first-1, this.second)
            Direction.SOUTH -> Pair(this.first+1, this.second)
            Direction.WEST -> Pair(this.first, this.second-1)
            Direction.EAST -> Pair(this.first, this.second+1)
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
        loopSet: HashSet<Pair<Int, Int>>,
        currentTile: Pair<Int, Int>,
        input: List<List<Char>>
    ): List<Pair<Int, Int>> {
        val rowTiles = loopSet.filter { it.first == currentTile.first && it.second < currentTile.second }
        if (rowTiles.isEmpty()) return emptyList()
        val nextTile = rowTiles.maxWith(compareBy { it.second })
        return input[currentTile.first].withIndex()
            .filter { nextTile.second < it.index && it.index < currentTile.second }
            .map { Pair(currentTile.first, it.index) }
    }

    fun right(
        loopSet: HashSet<Pair<Int, Int>>,
        currentTile: Pair<Int, Int>,
        input: List<List<Char>>
    ): List<Pair<Int, Int>> {
        val rowTiles = loopSet.filter { it.first == currentTile.first && it.second > currentTile.second }
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
        val loopSet = loop.toHashSet()

        val newStart = loop.minWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second })
        val startIndex = loop.indexOf(newStart)
        val counterClockwise = loop[startIndex+1].first > newStart.first || loop[startIndex+1].second < newStart.second
        val enclosedTiles = mutableSetOf<Pair<Int,Int>>()
        for (i in loop.indices) {
            val idx = (i+startIndex)%loop.size
            val currentTile = loop[idx]
            val prevTile = loop[(idx + loop.size - 1) % loop.size]
            val currentChar = input[currentTile.first][currentTile.second]
            when (currentChar) {
                'L' -> if (!counterClockwise && prevTile.first < currentTile.first
                    || counterClockwise && prevTile.second > currentTile.second)
                    enclosedTiles.addAll(left(loopSet, currentTile, input))
                '7' -> if (!counterClockwise && prevTile.first > currentTile.first
                    || counterClockwise && prevTile.second < currentTile.second)
                    enclosedTiles.addAll(right(loopSet, currentTile, input))
                'F' -> if (!counterClockwise && prevTile.second > currentTile.second
                    || counterClockwise && prevTile.first > currentTile.first)
                    enclosedTiles.addAll(left(loopSet, currentTile, input))
                'J' -> if (!counterClockwise && prevTile.second < currentTile.second
                    || counterClockwise && prevTile.first < currentTile.first)
                    enclosedTiles.addAll(right(loopSet, currentTile, input))
                '|' -> {
                    if (!counterClockwise == prevTile.first < currentTile.first)
                        enclosedTiles.addAll(left(loopSet, currentTile, input))
                    else if (!counterClockwise == prevTile.first > currentTile.first)
                        enclosedTiles.addAll(right(loopSet, currentTile, input))
                }
            }
        }
        return enclosedTiles.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day10_test").map { it.toList() }
    check(part1(testInput1) == 8)
    val testInput2 = readInput("Day10_test2").map { it.toList() }
    check(part2(testInput2) == 8)
    val testInput3 = readInput("Day10_test3").map { it.toList() }
    check(part2(testInput3) == 10)

    val input = readInput("Day10").map { it.toList() }
    part1(input).println()
    part2(input).println()
}
