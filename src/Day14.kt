import java.lang.Integer.max
import java.util.SortedSet
import kotlin.math.min

enum class RockType {
    ROUND,
    SQUARE
}

data class Rock(var row: Int, var column: Int, val type: RockType): Comparable<Rock> {

    fun isNorthOf(other: Rock): Boolean = this.row < other.row && this.column == other.column
    fun isWestOf(other: Rock): Boolean = this.row == other.row && this.column < other.column
    fun isSouthOf(other: Rock): Boolean = this.row > other.row && this.column == other.column
    fun isEastOf(other: Rock): Boolean = this.row == other.row && this.column > other.column
    override fun compareTo(other: Rock): Int {
        val tc = this.type.compareTo(other.type)
        if (tc != 0) return tc
        val rc = this.row.compareTo(other.row)
        if (rc != 0) return rc
        return this.column.compareTo(other.column)
    }
}

fun tiltNorth(roundRocks: SortedSet<Rock>, squareRocks: SortedSet<Rock>): SortedSet<Rock> {
    val newSet = roundRocks.map { Rock(it.row, it.column, it.type) }.toSortedSet()
    for (rock in newSet) {
        rock.row = max(
            0,
            max(
                newSet.filter { it.isNorthOf(rock) }.maxOrNull()?.row?.plus(1) ?: 0,
                squareRocks.filter { it.isNorthOf(rock) }.maxOrNull()?.row?.plus(1) ?: 0
            )
        )
    }
    return newSet
}

fun tiltWest(roundRocks: SortedSet<Rock>, squareRocks: SortedSet<Rock>): SortedSet<Rock> {
    val newSet = roundRocks.map { Rock(it.row, it.column, it.type) }.toSortedSet()
    for (rock in newSet) {
        rock.column = max(
            0,
            max(
                newSet.filter { it.isWestOf(rock) }.maxOrNull()?.column?.plus(1) ?: 0,
                squareRocks.filter { it.isWestOf(rock) }.maxOrNull()?.column?.plus(1) ?: 0
            )
        )
    }
    return newSet
}

fun tiltSouth(roundRocks: SortedSet<Rock>, squareRocks: SortedSet<Rock>, height: Int): SortedSet<Rock> {
    val newSet = roundRocks.map { Rock(it.row, it.column, it.type) }.toSortedSet()
    for (rock in newSet.reversed()) {
        rock.row = min(
            height,
            min(
                newSet.filter { it.isSouthOf(rock) }.minOrNull()?.row?.minus(1) ?: height,
                squareRocks.filter { it.isSouthOf(rock) }.minOrNull()?.row?.minus(1) ?: height
            )
        )
    }
    return newSet
}

fun tiltEast(roundRocks: SortedSet<Rock>, squareRocks: SortedSet<Rock>, width: Int): SortedSet<Rock> {
    val newSet = roundRocks.map { Rock(it.row, it.column, it.type) }.toSortedSet()
    for (rock in newSet.reversed()) {
        rock.column = min(
            width,
            min(
                newSet.filter { it.isEastOf(rock) }.minOrNull()?.column?.minus(1) ?: width,
                squareRocks.filter { it.isEastOf(rock) }.minOrNull()?.column?.minus(1) ?: width
            )
        )
    }
    return newSet
}

fun cycleDish(roundRocks: SortedSet<Rock>, squareRocks: SortedSet<Rock>, height: Int, width: Int): SortedSet<Rock> {
    val step1 = tiltNorth(roundRocks, squareRocks)
    val step2 = tiltWest(step1, squareRocks)
    val step3 = tiltSouth(step2, squareRocks, height)
    val step4 = tiltEast(step3, squareRocks, width)
    return step4
}

fun main() {
    fun mapInput(input: List<String>) =
        input.flatMapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, column ->
                when (column) {
                    'O' -> Rock(rowIndex, columnIndex, RockType.ROUND)
                    '#' -> Rock(rowIndex, columnIndex, RockType.SQUARE)
                    else -> null
                }
            }.filterNotNull()
        }.groupBy { it.type }

    fun part1(input: List<String>): Int {
        val mappedInput = mapInput(input)

        val round = mappedInput[RockType.ROUND]!!.toSortedSet()
        val square = mappedInput[RockType.SQUARE]!!.toSortedSet()
        val result = tiltNorth(round, square)
        return result.sumOf { input.size - it.row }
    }

    fun part2(input: List<String>): Int {
        val mappedInput = mapInput(input)

        val round = mappedInput[RockType.ROUND]!!.toSortedSet()
        val square = mappedInput[RockType.SQUARE]!!.toSortedSet()
        val cycles = mutableListOf(round)
        for (i in 1..1000_000_000) {
            val newSet = cycleDish(cycles.last(), square, input.size -1, input[0].length -1)
            val existingIndex = cycles.indexOf(newSet)
            if (existingIndex != -1) {
                return cycles[existingIndex + (1000_000_000-i)%(i-existingIndex)].sumOf { input.size - it.row }
            }
            cycles.addLast(newSet)
        }
        return cycles.last().sumOf { input.size - it.row }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
