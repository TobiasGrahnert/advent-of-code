fun main() {
    data class Lens(val label: String, var focalLength: Int)

    data class Box(val code: Int, val lenses: MutableList<Lens> = mutableListOf()) {
        fun add(lens: Lens) {
            this.lenses.firstOrNull { it.label == lens.label }?.also { it.focalLength = lens.focalLength } ?: this.lenses.addLast(lens)
        }
        fun remove(label: String) {
            this.lenses.removeIf { it.label == label }
        }
    }

    fun hash(text: String): Int {
        return text.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
    }

    fun part1(input: List<String>): Int {
        val texts = input[0].split(",")
        val hashes = texts.map { hash(it) }
        return hashes.sum()
    }

    fun part2(input: List<String>): Int {
        val instructions = input[0].split(",")
        val boxes = mutableMapOf<Int, Box>()
        for (instruction in instructions) {
            val (label, focalLength) = instruction.split('=','-')
            val hash = hash(label)
            if (focalLength.isBlank()) {
                boxes[hash]?.remove(label)
            } else {
                boxes.computeIfAbsent(hash) { Box(it) }.add(Lens(label, focalLength.toInt()))
            }
        }
        val results = boxes.filter { it.value.lenses.isNotEmpty() }.flatMap { it.value.lenses.mapIndexed { idx, lens -> Triple(it.key+1, idx+1, lens) } }
        return results.sumOf { it.first * it.second * it.third.focalLength }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
