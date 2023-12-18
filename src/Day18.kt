import java.security.InvalidParameterException
import java.util.*

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

    fun addNodes(nodes: MutableList<Pair<Long,Long>>, instruction: Instruction) {
        val rowMod = when (instruction.direction) {
            Direction.NORTH -> -1L
            Direction.SOUTH -> 1L
            else -> 0L
        }
        val colMod = when (instruction.direction) {
            Direction.WEST -> -1L
            Direction.EAST -> 1L
            else -> 0L
        }
        for (i in 1..instruction.length) {
            val sourceNode = nodes.first()
            nodes.addFirst(Pair(sourceNode.first + rowMod, sourceNode.second + colMod))
        }
    }

    fun Pair<Long,Long>.getDirectionTo(location: Pair<Long,Long>): Direction {
        if (this.first != location.first && this.second != location.second) throw InvalidParameterException()
        return when {
            this.first == location.first && this.second < location.second -> return Direction.EAST
            this.first == location.first && this.second > location.second -> return Direction.WEST
            this.second == location.second && this.first < location.first -> return Direction.SOUTH
            this.second == location.second && this.first > location.first -> return Direction.NORTH
            else -> Direction.NONE
        }
    }

    fun left(
        loop: List<Pair<Long, Long>>,
        currentTile: Pair<Long, Long>
    ): Long {
        val rowTiles = loop.filter { it.first == currentTile.first && it.second < currentTile.second }
        if (rowTiles.isEmpty()) return 0L
        val nextTile = rowTiles.maxWith(compareBy { it.second })
        return ((nextTile.second + 1L ) ..< currentTile.second).fold(0L) { acc, _ -> acc.inc() }
    }

    fun right(
        loop: List<Pair<Long, Long>>,
        currentTile: Pair<Long, Long>
    ): Long {
        val rowTiles = loop.filter { it.first == currentTile.first && it.second > currentTile.second }
        if (rowTiles.isEmpty()) return 0L
        val nextTile = rowTiles.minWith(compareBy { it.second })
        return ((currentTile.second + 1L) ..< nextTile.second).fold(0L) { acc, _ -> acc.inc() }
    }

    fun processInstructions(instructions: List<Instruction>): Long {
        val nodes = mutableListOf(Pair(0L, 0L))
        for (inst in instructions) {
            addNodes(nodes, inst)
        }

        nodes.removeFirst()
        val (startIndex, newStart) = nodes.withIndex()
            .minWith(compareBy<IndexedValue<Pair<Long, Long>>> { it.value.first }.thenBy { it.value.second })
        val counterClockwise =
            nodes[(startIndex + 1) % nodes.size].first > newStart.first || nodes[(startIndex + 1) % nodes.size].second < newStart.second
        val enclosedTiles = nodes.indices.asSequence().map {
            val idx = (it + startIndex) % nodes.size
            val currentTile = nodes[idx]
            val prevTile = nodes[(idx + nodes.size - 1) % nodes.size]
            val adjacentTileDirections = setOf(
                currentTile.getDirectionTo(prevTile),
                currentTile.getDirectionTo(nodes[(idx + nodes.size + 1) % nodes.size])
            )
            when (adjacentTileDirections) {
                setOf(Direction.NORTH, Direction.EAST) -> if (!counterClockwise && prevTile.first < currentTile.first
                    || counterClockwise && prevTile.second > currentTile.second
                )
                    left(nodes, currentTile)
                else 0

                setOf(Direction.SOUTH, Direction.WEST) -> if (!counterClockwise && prevTile.first > currentTile.first
                    || counterClockwise && prevTile.second < currentTile.second
                )
                    right(nodes, currentTile)
                else 0

                setOf(Direction.SOUTH, Direction.EAST) -> if (!counterClockwise && prevTile.second > currentTile.second
                    || counterClockwise && prevTile.first > currentTile.first
                )
                    left(nodes, currentTile)
                else 0

                setOf(Direction.NORTH, Direction.WEST) -> if (!counterClockwise && prevTile.second < currentTile.second
                    || counterClockwise && prevTile.first < currentTile.first
                )
                    right(nodes, currentTile)
                else 0

                setOf(Direction.NORTH, Direction.SOUTH) -> {
                    if (!counterClockwise == prevTile.first < currentTile.first)
                        left(nodes, currentTile)
                    else if (!counterClockwise == prevTile.first > currentTile.first)
                        right(nodes, currentTile)
                    else 0L
                }

                else -> 0L
            }
        }

        return enclosedTiles.sum()/2 + nodes.size
    }

    fun part1(input: List<String>): Long {
        val instructions = input.map { it.split(" ") }.map { Instruction(charToDirection(it[0]), it[1].toLong(), it[2].trim('(',')','#')) }
        return processInstructions(instructions)
    }

    fun part2(input: List<String>): Long {
        val instructions = input.map { it.split(" ") }.map { Instruction(charToDirection(it[0]), it[1].toLong(), it[2].trim('(',')','#')).getTrueInstruction() }
        return processInstructions(instructions)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
