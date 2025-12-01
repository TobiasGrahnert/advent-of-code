import kotlin.math.max
import kotlin.math.min

fun main() {
    val instructionRegex = """([ARa-z]+)([<>])?(\d*)?:?([ARa-z]*)?""".toRegex()
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        val rating = x+m+a+s
    }
    data class Instruction(val field: String? = null, val comparator: String? = null, val value: Int? = null, val destination: String)
    data class Workflow(val name: String, val instructions: List<Instruction>) {
        fun nextStep(part: Part): String {
            for (instruction in instructions) {
                when (instruction.field) {
                    "x" -> if (instruction.comparator == ">" && part.x > instruction.value!! || instruction.comparator == "<" && part.x < instruction.value!!) return instruction.destination
                    "m" -> if (instruction.comparator == ">" && part.m > instruction.value!! || instruction.comparator == "<" && part.m < instruction.value!!) return instruction.destination
                    "a" -> if (instruction.comparator == ">" && part.a > instruction.value!! || instruction.comparator == "<" && part.a < instruction.value!!) return instruction.destination
                    "s" -> if (instruction.comparator == ">" && part.s > instruction.value!! || instruction.comparator == "<" && part.s < instruction.value!!) return instruction.destination
                    else -> return instruction.destination
                }
            }
            return instructions.last().destination
        }
    }

    fun buildWorkflow(line: String): Workflow{
        val (name, instructions) = line.split('{')
        return Workflow(name, instructions.trim('{', '}').split(',').mapNotNull { instructionRegex.matchEntire(it) }.map {
            if (it.groupValues.all { g -> g.isNotBlank() }) {
                Instruction(
                    it.groups[1]!!.value,
                    it.groups[2]!!.value,
                    it.groups[3]!!.value.toInt(),
                    it.groups[4]!!.value
                )
            } else {
                Instruction(destination = it.groups[1]!!.value)
            }
        })
    }

    fun checkPart(part: Part, workflows: Map<String,Workflow>): Boolean {
        var step = "in"
        while (step !in setOf("A","R")) {
            val workflow = workflows[step]
            step = workflow!!.nextStep(part)
        }
        return step == "A"
    }

    fun part1(input: List<String>): Int {
        val (workflowtext, partText) = input.joinToString("\n") { it }.split("\n\n")
        val workflows = workflowtext.split("\n").map { buildWorkflow(it) }.associateBy { it.name }
        val parts = partText.split("\n").map { it.split("\\D".toRegex()).filter { it.isNotBlank() } }.map { Part(it[0].toInt(), it[1].toInt(), it[2].toInt(), it[3].toInt()) }
        val results = parts.associateWith { checkPart(it, workflows) }
        return results.filter { it.value }.map { it.key.rating }.sum()
    }

    fun processWorkflows(
        workflows: Map<String,Workflow>,
        ranges: MutableMap<String, Pair<Int, Int>>,
        destination: String
    )
    {
        if (destination == "in") return
        val acceptFlows = workflows.filter { it.value.instructions.any { it.destination == destination } }.values
        for (workflow in acceptFlows) {
            val accept = workflow.instructions.filter { it.destination == destination }
            for (instruction in accept) {
                when (instruction.comparator) {
                    ">" -> ranges[instruction.field!!] = Pair(
                        max(ranges[instruction.field]!!.first, instruction.value!! + 1),
                        ranges[instruction.field]!!.second
                    )

                    "<" -> ranges[instruction.field!!] = Pair(
                        ranges[instruction.field]!!.first,
                        min(ranges[instruction.field]!!.second, instruction.value!! + 1)
                    )

                    else -> {
                        for (inst in workflow.instructions.filter { it.destination != "A" }) {
                            when (inst.comparator) {
                                "<" -> ranges[inst.field!!] =
                                    Pair(max(ranges[inst.field]!!.first, inst.value!!), ranges[inst.field]!!.second)

                                ">" -> ranges[inst.field!!] =
                                    Pair(ranges[inst.field]!!.first, min(ranges[inst.field]!!.second, inst.value!!))
                            }
                        }
                    }
                }
            }
            processWorkflows(workflows,ranges,workflow.name)
        }
    }

    fun part2(input: List<String>): Int {
        val (workflowtext, _) = input.joinToString("\n") { it }.split("\n\n")
        val workflows = workflowtext.split("\n").map { buildWorkflow(it) }.associateBy { it.name }
        val ranges = mutableMapOf("x" to Pair(1,4000), "m" to Pair(1,4000), "a" to Pair(1,4000), "s" to Pair(1,4000))
        processWorkflows(workflows, ranges, "A")
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 0)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
