data class Tile(val row: Int, val column: Int, val type: Char) {
    private val enteredDirections = mutableSetOf<Direction>()

    fun isEnergized(): Boolean = enteredDirections.isNotEmpty()

    fun getNextDirections(enteredDirection: Direction): List<Direction> {
        if (enteredDirection == Direction.NONE || !enteredDirections.add(enteredDirection)) {
            return listOf(Direction.NONE)
        }
        return when (type) {
            '.' -> listOf(enteredDirection)
            '/' -> when (enteredDirection) {
                Direction.NORTH -> listOf(Direction.EAST)
                Direction.EAST -> listOf(Direction.NORTH)
                Direction.SOUTH -> listOf(Direction.WEST)
                Direction.WEST -> listOf(Direction.SOUTH)
                else -> listOf(Direction.NONE)
            }
            '\\' -> when (enteredDirection) {
                Direction.NORTH -> listOf(Direction.WEST)
                Direction.EAST -> listOf(Direction.SOUTH)
                Direction.SOUTH -> listOf(Direction.EAST)
                Direction.WEST -> listOf(Direction.NORTH)
                else -> listOf(Direction.NONE)
            }
            '-' -> {
                if (enteredDirection in listOf(Direction.WEST, Direction.EAST)) listOf(enteredDirection)
                else listOf(Direction.WEST, Direction.EAST)
            }
            '|' -> {
                if (enteredDirection in listOf(Direction.NORTH, Direction.SOUTH)) listOf(enteredDirection)
                else listOf(Direction.NORTH, Direction.SOUTH)
            }
            else -> listOf(Direction.NONE)
        }
    }

    override fun toString(): String {
        if (enteredDirections.size > 1) {
            return enteredDirections.size.toString()
        }
        return if (this.type == '.') when (enteredDirections.firstOrNull()) {
            Direction.NORTH -> "^"
            Direction.EAST -> ">"
            Direction.SOUTH -> "V"
            Direction.WEST -> "<"
            else -> this.type.toString()
        } else this.type.toString()
    }
}

fun nextLocation(row: Int, column: Int, direction: Direction): Pair<Int,Int> {
    return when (direction) {
        Direction.NORTH -> Pair(row-1, column)
        Direction.SOUTH -> Pair(row+1, column)
        Direction.EAST -> Pair(row, column+1)
        Direction.WEST -> Pair(row, column-1)
        else -> Pair(row, column)
    }
}

fun main() {
    fun traceBeam(contraption: List<List<Tile>>, enteredDirection: Direction, startTile: Tile) {
        val nextDirections = startTile.getNextDirections(enteredDirection)
        val nextLocations = nextDirections.map { nextLocation(startTile.row, startTile.column, it) }
        for (location in nextLocations.withIndex()) {
            if (location.value.first !in contraption.indices
                || location.value.second !in contraption[startTile.row].indices
                || nextDirections[location.index] == Direction.NONE)
                continue
            traceBeam(contraption, nextDirections[location.index], contraption[location.value.first][location.value.second])
        }
    }

    fun getEnergy(contraption: List<List<Tile>>, enteredDirection: Direction, startPoint: Pair<Int,Int>): Int {
        traceBeam(contraption, enteredDirection, contraption[startPoint.first][startPoint.second])
        return contraption.flatten().count { it.isEnergized() }
    }

    fun parseTiles(input: List<String>) = input.mapIndexed { row, line -> line.mapIndexed { column, c -> Tile(row,column,c) } }

    fun part1(input: List<String>): Int {
        val tiles = parseTiles(input)
        return getEnergy(tiles, Direction.EAST, Pair(0,0))
    }

    fun part2(input: List<String>): Int {
        val startPoints = input.flatMapIndexed { row, s -> s.withIndex().filter { row == 0 || row == input.size - 1 || it.index == 0 || it.index == s.length - 1 }.map { iv -> Pair(row, iv.index) } }
        val maxRow = startPoints.maxOf { it.first }
        val maxColumn = startPoints.maxOf { it.second }
        val results = startPoints.flatMap {
            if (it.first == 0 && it.second == 0) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.EAST, it)),
                    Pair(it, getEnergy(parseTiles(input), Direction.SOUTH, it)))
            } else if (it.first == maxRow && it.second == maxColumn) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.WEST, it)),
                    Pair(it, getEnergy(parseTiles(input), Direction.NORTH, it)))
            } else if (it.first == 0) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.SOUTH, it)))
            } else if (it.second == 0) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.EAST, it)))
            } else if (it.first == maxRow) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.NORTH, it)))
            } else if (it.second == maxColumn) {
                listOf(Pair(it, getEnergy(parseTiles(input), Direction.WEST, it)))
            } else {
                emptyList()
            }
        }
        return results.maxOf { it.second }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
