import java.util.stream.Collectors

fun main() {
    data class Digit(val digitText:String, val digitNumber:Int, var position:Int = -1)

    val regex = "^\\D*(\\d).*?(\\d?)\\D*\$".toRegex()
    val digits = listOf(
        Digit("one", 1),
        Digit("two", 2),
        Digit("three", 3),
        Digit("four", 4),
        Digit("five", 5),
        Digit("six", 6),
        Digit("seven", 7),
        Digit("eight", 8),
        Digit("nine", 9),
        Digit("1", 1),
        Digit("2", 2),
        Digit("3", 3),
        Digit("4", 4),
        Digit("5", 5),
        Digit("6", 6),
        Digit("7", 7),
        Digit("8", 8),
        Digit("9", 9),
        )

    fun part1(input: List<String>): Int {
         return input.parallelStream()
             .map {regex.matchEntire(it)?.destructured }
             .filter { it != null }
             .map { "${it!!.component1()}${it.component2().ifEmpty { it.component1() }}" }
             .collect(Collectors.summingInt { it.toInt() })
    }

    fun convertLineToNumber(line: String): Int {
        var numberString = ""
        for (entry in digits) {
            entry.position = line.indexOf(entry.digitText)
        }
        digits.filter { it.position >= 0 }.minByOrNull { it.position }?.let {
            numberString += it.digitNumber
        }
        for (entry in digits) {
            entry.position = line.lastIndexOf(entry.digitText)
        }
        digits.filter { it.position >= 0 }.maxByOrNull { it.position }?.let {
            numberString += it.digitNumber
        }
        return numberString.toInt()
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { convertLineToNumber(it) }
    }



    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_test")
    check(part1(testInput1) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
