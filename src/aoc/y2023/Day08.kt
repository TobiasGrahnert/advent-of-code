package aoc.y2023

import println
import readInput
import findLCM
fun main() {

    val dir = hashSetOf('L','R')

    fun getNextNode(
        map: Map<String, List<String>>,
        currentNode: String,
        direction: Char
    ): String {
        val nextNode = map[currentNode]?.get(dir.indexOf(direction))
        if (nextNode == null || nextNode == currentNode) {
            "DeadEnd reached".println()
            throw Exception("Error")
        }
        return nextNode
    }

    fun part1(input: List<String>): Int {
        val map =input.drop(2).associate { line ->
            val (id, value) = line.split("=").map { it.trim(' ', '(', ')') }
            Pair(id, value.split(",").map { it.trim() })
        }.filter { entry -> entry.value.any { it != entry.key } }.toMap()
        val directions = input.take(1).flatMap { it.toList() }
        var currentNode = "AAA"
        var steps = 0
        while (currentNode != "ZZZ") {
            val direction = directions[steps % directions.size]
            currentNode = getNextNode(map, currentNode, direction)
            steps++
        }
        return steps
    }

    fun part2(input: List<String>): Long {
        val map =input.drop(2).associate { line ->
            val (id, value) = line.split("=").map { it.trim(' ', '(', ')') }
            Pair(id, value.split(",").map { it.trim() })
        }.filter { entry -> entry.value.any { it != entry.key } }.toMap()
        val directions = input.take(1).flatMap { it.toList() }
        var currentNodes = map.keys.filter { it.endsWith("A") }.associateWith { 0L }
        var steps = 0L
        while (currentNodes.any { !it.key.endsWith("Z") }) {
            val direction = directions[(steps % directions.size).toInt()]
            val nextNodes = currentNodes.map {
                if (it.key.endsWith("Z")) {
                    it.key to it.value
                } else {
                    getNextNode(map, it.key, direction) to it.value.inc()
                }
            }.toMap()
            currentNodes = nextNodes
            steps++
        }
        return currentNodes.map { it.value }.fold(1L) { a, b -> findLCM(a,b) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2023,"Day08_test")
    check(part1(testInput) == 2)
    val testInput2 = readInput(2023,"Day08_test2")
    check(part2(testInput2) == 6L)

    val input = readInput(2023,"Day08")
    part1(input).println()
    part2(input).println()
}
