package day20

import readInput

typealias Image = Map<Int, Map<Int, Int>>

class Solver(
    private val algo: List<Int>
) {
    fun evolve(steps: Int, input: Image): Image {
        var from: Map<Int, MutableMap<Int, Int>> = input.mapValues { it.value.toMutableMap() }
        var implicit = 0

        repeat(steps) {
            from = buildMap {
                val minY = from.minOf { it.key } - 1
                val minX = from.minOf { it.value.minOf { r -> r.key } } - 1
                val maxY = from.maxOf { it.key } + 1
                val maxX = from.maxOf { it.value.maxOf { r -> r.key } } + 1

                for (y in minY..maxY) {
                    for (x in minX..maxX) {
                        val n0 = from[y - 1]?.get(x - 1) ?: implicit
                        val n1 = from[y - 1]?.get(x) ?: implicit
                        val n2 = from[y - 1]?.get(x + 1) ?: implicit
                        val n3 = from[y]?.get(x - 1) ?: implicit
                        val n4 = from[y]?.get(x) ?: implicit
                        val n5 = from[y]?.get(x + 1) ?: implicit
                        val n6 = from[y + 1]?.get(x - 1) ?: implicit
                        val n7 = from[y + 1]?.get(x) ?: implicit
                        val n8 = from[y + 1]?.get(x + 1) ?: implicit
                        val s = "$n0$n1$n2$n3$n4$n5$n6$n7$n8"
                        val n = s.toInt(radix = 2)
                        val r = algo[n]
                        this.getOrPut(y) { mutableMapOf() }[x] = r
                    }
                }
            }
            implicit = if (implicit == 0) algo.first() else algo.last()
        }

        return from
    }
}

fun parse(input: List<String>): Pair<List<Int>, Image> {
    val algo = input.first().toCharArray().map { if (it == '#') 1 else 0 }
    val puzzle = mutableMapOf<Int, Map<Int, Int>>()
    var i = 0

    input.drop(2).forEach { line ->
        puzzle[i] = line
            .toCharArray()
            .withIndex()
            .associate { it.index to if (it.value == '#') 1 else 0 }
        i += 1
    }

    return algo to puzzle
}

fun main() {
    fun part1(input: List<String>): Int {
        val (algo, puzzle) = parse(input)
        val solver = Solver(algo)
        val solution = solver.evolve(2, puzzle)
        return solution.values.sumOf { it.values.sum() }
    }

    fun part2(input: List<String>): Int {
        val (algo, puzzle) = parse(input)
        val solver = Solver(algo)
        val solution = solver.evolve(50, puzzle)
        return solution.values.sumOf { it.values.sum() }
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20")
    println(part1(input)) // 5361
    println(part2(input)) // 16826
}
