import kotlin.streams.asStream

fun main() {

    fun findSolutions(cache:MutableMap<Triple<List<Char>, List<Int>, Int>, Long>, line:List<Char>, sizes:List<Int>, doneInGroup:Int):Long {
        val cv = cache[Triple(line,sizes,doneInGroup)]
        if (cv != null) {
            return cv
        }
        if (line.isEmpty()) {
            return if (sizes.isEmpty() && (doneInGroup == 0)) 1 else 0
        }
        var solutions = 0L
        val possible = if (line[0] == '?') listOf('.','#') else line.subList(0,1)
        for (c in possible) {
            if (c == '#') {
                solutions += findSolutions(cache, line.subList(1, line.size), sizes, doneInGroup + 1)
            }
            else {
                if (doneInGroup > 0) {
                    if (sizes.isNotEmpty() && sizes[0] == doneInGroup)
                        solutions += findSolutions(cache, line.subList(1, line.size), sizes.subList(1, sizes.size), 0)
                }
                else {
                    solutions += findSolutions(cache, line.subList(1, line.size), sizes, 0)
                }
            }
        }
        cache[Triple(line, sizes, doneInGroup)] = solutions
        return solutions
    }

    tailrec fun replaceQuestionmark(lines:Sequence<String>, count:Int):Sequence<String> {
        if (count > 0) {
            return replaceQuestionmark(lines.flatMap { listOf(it.replaceFirst('?','.'), it.replaceFirst('?', '#')) }, count-1)
        }
        return lines
    }

    data class Record(val text:String, val groups:List<Int>) {
        private val possibilities = replaceQuestionmark(sequenceOf(text), text.count { it == '?' })
        val possibleGroups = possibilities.asStream().parallel().map { it.split(".").filter { it.isNotBlank() }.map { it.length } }.filter { it == groups }
        val possibilitiesCount = findSolutions(mutableMapOf(),"$text.".toList(), groups, 0)
    }

    fun parseInput(row:String): Record {
        val (text, groups) = row.split(" ")
        return Record(text, groups.split(",").map { it.toInt() })
    }

    fun parseInput2(row:String): Record {
        val (text, groups) = row.split(" ")
        val intGroups = groups.split(",").map { it.toInt() }
        return Record("$text?$text?$text?$text?$text", listOf(intGroups,intGroups,intGroups,intGroups,intGroups).flatten())
    }

    fun part1(input: List<String>): Long {
        val records = input.map { parseInput(it) }
        return records.sumOf { it.possibleGroups.count() }
    }

    fun part2(input: List<String>): Long {
        val records = input.asSequence().map { parseInput2(it) }
        return records.sumOf { it.possibilitiesCount }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
