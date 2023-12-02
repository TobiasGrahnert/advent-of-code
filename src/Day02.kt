
data class Pull(var red: Int = 0, var green: Int = 0, var blue: Int = 0)
data class Game(val id: Int, val pulls: List<Pull>) {
    fun minPower(): Int {
        return pulls.maxOf { it.red } * pulls.maxOf { it.blue } * pulls.maxOf { it.green }
    }
}


fun main() {
    val regex = """\D*(\d+)\D*""".toRegex()

    fun String.toGame(): Game {
        val gameParts = this.split(":")
        val (id) = regex.matchEntire(gameParts[0])?.destructured!!
        val game = Game(id.toInt(), mutableListOf())
        val pullParts = gameParts[1].split(";")
        for (part in pullParts) {
            val pull = Pull()
            for (color in part.split(",")) {
                val (count)= regex.matchEntire(color)?.destructured!!
                if (color.contains("red")) {
                    pull.red = count.toInt()
                } else if (color.contains("blue")) {
                    pull.blue = count.toInt()
                } else if (color.contains("green")) {
                    pull.green = count.toInt()
                }
            }
            game.pulls.addLast(pull)
        }
        return game
    }

    fun part1(input: List<String>): Int {
        val maxRed = 12
        val maxBlue = 14
        val maxGreen = 13
        return input.map { it.toGame() }
                .filter { game -> game.pulls.all { pull -> pull.red <= maxRed && pull.blue <= maxBlue && pull.green <= maxGreen } }
                .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.toGame() }
                .sumOf { it.minPower() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
