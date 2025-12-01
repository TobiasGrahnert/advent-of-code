package aoc.y2023

import println
import readInput
import java.util.stream.Collectors
import kotlin.streams.asStream

fun main() {

    fun readMaps(
        input: List<String>
    ):List<List<Pair<LongRange,LongRange>>> {
        var idx = -1
        val maps = mutableListOf<MutableList<Pair<LongRange, LongRange>>>()
        for (index in 1..<input.size) {
            val line = input[index]
            if (line.isBlank()) continue
            if (line.contains("map")) {
                maps.add(mutableListOf())
                idx++
                continue
            }
            val (dest, source, range) = line.split(" ").filter { it.isNotBlank() }.map { it.toLong() }
            maps[idx].addLast(dest..<dest+range to source..<source+range)
        }
        return maps
    }

    fun mapSeed(seed: Long, seedMap: List<Pair<LongRange,LongRange>>):Long {
        for (mapping in seedMap) {
            if (seed in mapping.second) return seed - mapping.second.first + mapping.first.first
        }
        return seed
    }

    fun mapSeeds(seed: Long, seedMaps: List<List<Pair<LongRange,LongRange>>>):Long {
        var retVal = seed
        for (seedMap in seedMaps) {
            retVal = mapSeed(retVal, seedMap)
        }
        return retVal
    }

    fun part1(input: List<String>): Long {
        val maps = readMaps(input)
        val seeds = input[0].split(":")[1].split(" ").filter { it.isNotBlank() }.map { mapSeeds(it.toLong(), maps) }
        return seeds.minOf { it }
    }

    fun part2(input: List<String>): Long {
        val maps = readMaps(input)

        return input[0].split(":")[1].split(" ").asSequence().filter { it.isNotBlank() }.map { it.toLong() }.chunked(2).map { it[0]..< it[0]+it[1] }.flatten().asStream().parallel().map {
            mapSeeds(it, maps)
        }.collect(Collectors.minBy(Comparator.comparingLong { it })).get()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2023,"Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput(2023,"Day05")
    part1(input).println()
    part2(input).println()
}
