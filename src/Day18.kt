import java.util.*
import kotlin.math.abs

fun main() {
    data class Instruction(val direction: Direction, val length: Long, val color: String) {
        fun getTrueInstruction(): Instruction {
            val direction = when (color.last()) {
                '3' -> Direction.NORTH
                '1' -> Direction.SOUTH
                '2' -> Direction.WEST
                '0' -> Direction.EAST
                else -> Direction.NONE
            }
            val length = color.substring(0, color.length-1).toLong(16)
            return Instruction(direction, length, this.color)
        }
    }

    fun charToDirection(char: String): Direction {
        return when(char.uppercase(Locale.getDefault())) {
            "U" -> Direction.NORTH
            "D" -> Direction.SOUTH
            "L" -> Direction.WEST
            "R" -> Direction.EAST
            else -> Direction.NONE
        }
    }

    fun calculateEnclosedArea(instructions: List<Instruction>): Long {
        val nodes = mutableListOf<Triple<Long, Long, Direction>>()
        for (inst in instructions) {
            val lastNode = nodes.lastOrNull() ?: Triple(0L, 0L, Direction.NONE)
            when (inst.direction) {
                Direction.NORTH -> nodes.addLast(Triple(lastNode.first + inst.length, lastNode.second, inst.direction))
                Direction.SOUTH -> nodes.addLast(Triple(lastNode.first - inst.length, lastNode.second, inst.direction))
                Direction.WEST -> nodes.addLast(Triple(lastNode.first, lastNode.second - inst.length, inst.direction))
                Direction.EAST -> nodes.addLast(Triple(lastNode.first, lastNode.second + inst.length, inst.direction))
                else -> continue
            }
        }
        val mostLeft = abs(nodes.minOf { it.second })
        val mostDown = abs(nodes.minOf { it.first })
        val offsetNodes = nodes.map { Triple(it.first + mostDown, it.second + mostLeft, it.third) }
        val (startIndex, newStart) = offsetNodes.withIndex()
            .minWith(compareBy<IndexedValue<Triple<Long, Long, Direction>>> { it.value.first }.thenBy { it.value.second })
        val counterClockwise =
            offsetNodes[(startIndex + 1) % offsetNodes.size].first > newStart.first || offsetNodes[(startIndex + 1) % offsetNodes.size].second < newStart.second
        val area = offsetNodes.indices.map {
            val idx = (it + startIndex) % offsetNodes.size
            val currentTile = offsetNodes[idx]
            val prevTile = offsetNodes[(idx + offsetNodes.size - 1) % offsetNodes.size]
            when (currentTile.third) {
                Direction.WEST -> {
                    if (counterClockwise) (currentTile.second - prevTile.second) * (currentTile.first)
                    else (currentTile.second - prevTile.second) * (currentTile.first + 1)
                }

                Direction.EAST -> {
                    if (counterClockwise) (currentTile.second - prevTile.second) * (currentTile.first + 1)
                    else (currentTile.second - prevTile.second) * (currentTile.first)
                }

                Direction.SOUTH -> if (counterClockwise) (prevTile.first - currentTile.first) else 0
                Direction.NORTH -> if (!counterClockwise) (currentTile.first - prevTile.first) else 0
                else -> 0
            }
        }

        return area.sum() + 1
    }

    fun part1(input: List<String>): Long {
        val instructions = input.map { it.split(" ") }.map { Instruction(charToDirection(it[0]), it[1].toLong(), it[2].trim('(',')','#')) }
        return calculateEnclosedArea(instructions)
    }

    fun part2(input: List<String>): Long {
        val instructions = input.map { it.split(" ") }.map { Instruction(charToDirection(it[0]), it[1].toLong(), it[2].trim('(',')','#')).getTrueInstruction() }
        return calculateEnclosedArea(instructions)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
