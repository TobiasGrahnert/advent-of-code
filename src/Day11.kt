import kotlin.math.abs

fun main() {

    fun expandGalaxies(
        galaxies: List<Pair<Long, Long>>,
        emptyRows: List<Long>,
        emptyColumns: List<Long>,
        expansion: Long = 2
    ) =
        galaxies.map {
            Pair(it.first + emptyRows.count { r -> r < it.first } * (expansion - 1),
                it.second + emptyColumns.count { c -> c < it.second } * (expansion - 1))
        }

    fun getDistance(point1:Pair<Long,Long>, point2: Pair<Long,Long>):Long {
        return abs(point2.first - point1.first) + abs(point2.second - point1.second)
    }

    fun solve(input: List<String>, expansion:Long = 2): Long {
        val galaxies = input.flatMapIndexed { y, s -> s.mapIndexed { x, c -> if (c == '#') Pair(y.toLong(),x.toLong()) else null } }.filterNotNull()
        val emptyRows = input.withIndex().filter { !it.value.contains("#") }.map { it.index.toLong() }
        val emptyColumns = input.map { it.toList() }.transpose().withIndex().filter { !it.value.joinToString("") { c -> c.toString() }.contains("#") }.map { it.index.toLong() }
        val expandedGalaxies = expandGalaxies(galaxies, emptyRows, emptyColumns, expansion)
        val galaxyPairs = expandedGalaxies.flatMapIndexed { index, galaxy -> expandedGalaxies.drop(index + 1).map { Pair(galaxy, it) } }
        val distances = galaxyPairs.map { getDistance(it.first, it.second) }
        return distances.sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(solve(testInput) == 374L)
    check(solve(testInput, 10) == 1030L)
    check(solve(testInput, 100) == 8410L)

    val input = readInput("Day11")
    solve(input).println()
    solve(input, 1000000).println()
}
