fun main() {
    fun getDifferences(numbers: List<Int>): List<List<Int>> {
        val differences = mutableListOf(numbers)
        while (differences.first().isNotEmpty() && differences.first().any { it != 0 }) {
            val diff = differences.first().windowed(2).map { it.last() - it.first() }
            differences.addFirst(diff)
        }
        return differences
    }

    fun getNext(numbers: List<Int>): Int {
        val diffs = getDifferences(numbers)
        return diffs.sumOf { it.last() }
    }

    fun getPrevious(numbers: List<Int>): Int {
        val diffs = getDifferences(numbers)
        return diffs.fold(0) { acc, it -> it.first() - acc }
    }

    fun part1(input: List<String>): Int {
        val numbers = input.map { it.split(" ").map { n -> n.toInt() } }
        val nextNumbers = numbers.map { getNext(it) }
        return nextNumbers.sum()
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { it.split(" ").map { n -> n.toInt() } }
        val prevNumbers = numbers.map { getPrevious(it) }
        return prevNumbers.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
