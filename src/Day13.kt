import kotlin.math.min

fun main() {

    fun countToSymmetrical(first: List<List<Char>>, second: List<List<Char>>): Int {
        return (0..< min(first.size,second.size)).sumOf { first[it].zip(second[it]) { a, b -> if (a != b) 1 else 0 }.sum() }
    }

    fun symmetryIndexWithCount(matrix: List<List<Char>>): Set<Pair<Int,Int>> {
        val list = mutableSetOf<Pair<Int, Int>>()
        val centerIdx = (matrix.size+1)/2
        for (i in 0..<centerIdx) {
            for (sign in intArrayOf(-1,1)) {
                val top = matrix.subList(0, centerIdx + i*sign).reversed()
                val bottom = matrix.subList(centerIdx + i*sign, matrix.size)
                if (top.isEmpty() || bottom.isEmpty()) continue
                list.add(Pair(centerIdx + i*sign, countToSymmetrical(top, bottom)))
            }
        }
        return list
    }

    data class Block(val text:String) {
        val characters = text.split("\n").map { it.toList() }
        private val symmetryIndexesWithCount = sequenceOf(characters, characters.transpose()).map { symmetryIndexWithCount(it) }
        val symmetry = symmetryIndexesWithCount
            .mapIndexed { idx, it -> idx to it.filter { it.second == 0 } }
            .filter { it.second.isNotEmpty() }
            .map { if (it.first == 0) it.second.first().first * 100 else it.second.first().first }
            .first { it > 0 }
        val newSymmetry = symmetryIndexesWithCount
            .mapIndexed { idx, it -> idx to it.filter { it.second == 1 } }
            .filter { it.second.isNotEmpty() }
            .map { if (it.first == 0) it.second.first().first * 100 else it.second.first().first }
            .first { it > 0 }
    }

    fun part1(input: List<Block>): Int {
        return input.sumOf { it.symmetry }
    }

    fun part2(input: List<Block>): Int {
        return input.sumOf { it.newSymmetry }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test").joinToString("\n") { it }.split("\n\n").filter { it.isNotBlank() }.map { Block(it) }
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13").joinToString("\n") { it }.split("\n\n").filter { it.isNotBlank() }.map { Block(it) }
    part1(input).println()
    part2(input).println()
}
