fun main() {
    val number = """\d+""".toRegex()
    val symbol = """[^\d.\s]""".toRegex()
    val gear = """\*""".toRegex()

    fun part1(input: List<String>): Int {
        var sum = 0
        input.forEachIndexed { index, line ->
            for (range in number.findAll(line)) {
                var toCheck = ""
                val start = (range.range.first - 1).coerceAtLeast(0)
                val end = (range.range.last + 2).coerceAtMost(line.length)
                if (index > 0) toCheck += input[index - 1].substring(start, end)
                toCheck += line[start]
                toCheck += line[end - 1]
                if (index < input.size - 1) toCheck += input[index + 1].substring(start, end)
                if (symbol.containsMatchIn(toCheck)) sum += range.value.toInt()
            }
        }
        return sum
    }

    fun HashMap<Pair<Int,Int>, List<Int>>.addEntry(key: Pair<Int,Int>, value: Int) {
        this.putIfAbsent(key, mutableListOf())
        this[key]?.addLast(value)
    }

    fun part2(input: List<String>): Int {
        val gears = hashMapOf<Pair<Int,Int>, List<Int>>()
        input.forEachIndexed { index, line ->
            for (range in number.findAll(line)) {
                val start = (range.range.first - 1).coerceAtLeast(0)
                val end = (range.range.last + 2).coerceAtMost(line.length)
                val rangeValue = range.value.toInt()
                if (index > 0) gear.findAll(input[index - 1].substring(start, end)).forEachIndexed { _, gearRange ->
                    gears.addEntry(Pair(index - 1, start + gearRange.range.first), rangeValue)
                }
                if (line[start] == '*') gears.addEntry(Pair(index, start), rangeValue)
                if (line[end - 1] == '*') gears.addEntry(Pair(index, end - 1), rangeValue)
                if (index < input.size - 1) gear.findAll(input[index + 1].substring(start, end)).forEachIndexed { _, gearRange ->
                    gears.addEntry(Pair(index + 1, start + gearRange.range.first), rangeValue)
                }
            }
        }
        return gears.filter { it.value.size == 2 }.map { it.value[0] * it.value[1] }.sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
