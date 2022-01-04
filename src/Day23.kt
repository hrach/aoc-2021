package day23

import readInput
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun parse(input: List<String>): Puzzle {
    val allowed = listOf('.', 'A', 'B', 'C', 'D')
    val match = input.joinToString("").toCharArray().filter { it in allowed }
    return Puzzle(
        positions = match.map {
            if (it == '.') null else it
        },
        price = 0,
        last = -1,
    )
}

data class Puzzle(
    val positions: List<Char?>,
    val price: Int,
    val last: Int,
) {
    init {
        check(positions.size == 19 || positions.size == 27)
    }

    private val isBig = positions.size == 27

    fun isFinal(): Boolean = correctPositions() == when (isBig) {
        true -> 4444
        false -> 44
    }

    fun correctPositions(): Int {
        var i = 0
        var a = 1

        for (j in 11 until positions.size step 4) {
            i += if (positions[j] == 'A') a else 0
            i += if (positions[j + 1] == 'B') a else 0
            i += if (positions[j + 2] == 'C') a else 0
            i += if (positions[j + 3] == 'D') a else 0
            a *= 10
        }

        return i
    }

    fun evolve(minPrice: Int, checked: Map<List<Char?>, Int>): List<Puzzle> = buildList {
        for ((i, char) in positions.withIndex()) {
            if (char == null || i == last) continue
            if (isCorrectPosition(char, i)) continue

            for (newPos in positions.indices) {
                val moves = positions.countMoves(char, from = i, to = newPos)
                if (moves == 0) continue

                val newPrice = price + moves * when (char) {
                    'A' -> 1
                    'B' -> 10
                    'C' -> 100
                    'D' -> 1000
                    else -> error("$char")
                }

                if (newPrice > minPrice) continue

                val newPositions = positions.toMutableList()
                newPositions[newPos] = char
                newPositions[i] = null

                val checkedPrice = checked[newPositions]
                if (checkedPrice != null && checkedPrice < newPrice) continue
                val newPuzzle = Puzzle(
                    positions = newPositions,
                    price = newPrice,
                    last = newPos,
                )
                add(newPuzzle)
            }
        }
    }

    private fun isCorrectPosition(c: Char, i: Int): Boolean {
        if (i < 11) return false

        val shift = when (c) {
            'A' -> 0
            'B' -> 1
            'C' -> 2
            'D' -> 3
            else -> error(c)
        }

        for (j in positions.size - 4 downTo 11 step 4) {
            if (j + shift == i) return true
            if (positions[j + shift] != c) return false
        }

        return false
    }

    private fun List<Char?>.countMoves(c: Char, from: Int, to: Int): Int {
        if (to == from) return 0

        // rule 1
        if (to == 2 || to == 4 || to == 6 || to == 8) return 0
        if (get(to) != null) return 0

        if (to >= 11) {
            // rule 2a && 2b
            val allowed = isCorrectPosition(c, to)
            if (!allowed) return 0
        } else {
            // rule 3
            if (from < 11) return 0
        }

        var i = from - 4
        while (i >= 11) {
            if (get(i) != null) return 0
            i -= 4
        }

        var j = to - 4
        while (j >= 11) {
            if (get(j) != null) return 0
            j -= 4
        }

        val a = when (from) {
            11, 15, 19, 23 -> 2
            12, 16, 20, 24 -> 4
            13, 17, 21, 25 -> 6
            14, 18, 22, 26 -> 8
            else -> from
        }
        val b = when (to) {
            11, 15, 19, 23 -> 2
            12, 16, 20, 24 -> 4
            13, 17, 21, 25 -> 6
            14, 18, 22, 26 -> 8
            else -> to
        }

        val range = min(a, b)..max(a, b)
        val isHallwayBlocked = range.any { this[it] != null && it != from }
        if (isHallwayBlocked) return 0

        return range.count() - 1 +
                when (from) {
                    in 23..26 -> 4
                    in 19..22 -> 3
                    in 15..18 -> 2
                    in 11..14 -> 1
                    else -> 0
                } +
                when (to) {
                    in 23..26 -> 4
                    in 19..22 -> 3
                    in 15..18 -> 2
                    in 11..14 -> 1
                    else -> 0
                }
    }

    fun print() {
        fun List<Char?>.p(pos: Int): String = get(pos)?.toString() ?: "."

        val rest = positions.drop(15).chunked(4).joinToString("\n") {
            """  #${it.p(0)}#${it.p(1)}#${it.p(2)}#${it.p(3)}#"""
        }

        with(positions) {
            println("#############")
            println("#${(0..10).joinToString("") { p(it) }}#")
            println("###${p(11)}#${p(12)}#${p(13)}#${p(14)}###")
            println(rest)
            println("  #########")
        }
        println()
    }
}

fun solve(inputPuzzle: Puzzle, minPrice: Int): Puzzle {
    val stack = PriorityQueue(
        compareByDescending<Puzzle> { it.correctPositions() }.then(compareBy { it.price })
    )
    stack.add(inputPuzzle)
    inputPuzzle.print()

    val checked = mutableMapOf<List<Char?>, Int>()

    var i = 0
    var minPuzzle: Puzzle? = null

    main@ while (stack.isNotEmpty()) {
        val puzzle = stack.poll()
        checked[puzzle.positions] = puzzle.price
        val nexts = puzzle.evolve(minPrice, checked)
        for (next in nexts) {
            if (next.isFinal()) {
                if (minPuzzle == null || minPuzzle.price > next.price) {
                    minPuzzle = next
                }
            } else {
                stack.add(next)
            }
        }
        if (minPuzzle != null) {
            i += 1
            if (i == 1_000_000) break@main
        }
    }

    return minPuzzle ?: error("not found")
}

fun main() {
    fun part1(input: List<String>, minPrice: Int): Int {
        return solve(parse(input), minPrice).price
    }

    fun part2(input: List<String>, minPrice: Int): Int {
        val newInput = input.toMutableList().apply {
            add(3, "  #D#C#B#A#")
            add(4, "  #D#B#A#C#")
        }
        return solve(parse(newInput), minPrice).price
    }

    val testInput = readInput("Day23_test")
    val input = readInput("Day23")

    println("---------- PART 1 ----------")
    check(part1(testInput, minPrice = 12521) == 12521)
    val part1 = part1(input, minPrice = 20000)
    println(part1)
    check(part1 == 13558)

    println("---------- PART 2 ----------")
    check(part2(testInput, minPrice = 44169) == 44169)
    val part2 = part2(input, minPrice = 70000)
    println(part2)
    check(part2 == 56982)
}
