package day18

import readInput
import kotlin.math.ceil
import kotlin.math.floor

data class Number(
    var value: Int,
    var depth: Int,
)

fun reduce(input: MutableList<Number>): Boolean {
    var changed = false
    do {
        val i = input.indexOfFirst { it.depth > 4 }
        if (i != -1) {
            changed = true
            val n0 = input.getOrNull(i - 1)
            val n1 = input[i]
            val n2 = input[i + 1]
            val n3 = input.getOrNull(i + 2)

            if (n0 != null) {
                n0.value += n1.value
            }
            if (n3 != null) {
                n3.value += n2.value
            }
            n1.value = 0
            n1.depth -= 1
            input.removeAt(i + 1)
            continue
        }

        val j = input.indexOfFirst { it.value >= 10 }
        if (j != -1) {
            changed = true
            val n1 = input[j]
            val leftVal = floor(n1.value / 2.0).toInt()
            val rightVal = ceil(n1.value / 2.0).toInt()
            n1.value = leftVal
            n1.depth += 1

            val n2 = Number(value = rightVal, depth = n1.depth)
            input.add(j + 1, n2)
        } else {
            break
        }
    } while (true)
    return changed
}

fun add(input: MutableList<Number>, toAdd: List<Number>) {
    toAdd.forEach { it.depth += 1 }
    input.forEach { it.depth += 1 }
    input.addAll(toAdd)
}

fun magnitude(input: List<Number>): Int {
    val data = input.toMutableList()
    while (data.size > 1) {
        val max = data.maxOf { it.depth }
        val i = data.indexOfFirst { it.depth == max }
        check(i != -1)
        val n1 = data[i]
        val a = n1.value
        if (i + 1 > data.size - 1) {
            println("toto")
        }
        val b = data[i + 1].value
        n1.value = 3 * a + 2 * b
        n1.depth -= 1
        data.removeAt(i + 1)
    }
    return data.first().value
}

fun parse(lines: List<String>): List<List<Number>> = buildList {
    lines.forEach { line ->
        var depth = 0
        val numbers = mutableListOf<Number>()
        line.toCharArray().forEach { char ->
            when (char) {
                '[' -> depth += 1
                ']' -> depth -= 1
                ',' -> {} // no-op
                else -> numbers.add(Number(value = char.digitToInt(), depth = depth))
            }
        }
        add(numbers)
    }
}

fun sum(numbers: List<List<Number>>): List<Number> {
    val result = numbers.first().toMutableList()
    numbers.drop(1).forEach {
        add(result, it)
        while (reduce(result)) {
            // no-op
        }
    }
    return result
}

fun List<Number>.copy() = map { it.copy() }

fun main() {
    fun part1(input: List<String>): Int {
        val numbers = parse(input)
        val result = sum(numbers)
        return magnitude(result)
    }

    fun part2(input: List<String>): Int {
        val numbers = parse(input)
        var mag = 0
        for ((i, a) in numbers.withIndex()) {
            for ((j, b) in numbers.withIndex()) {
                if (i == j) continue
                mag = maxOf(mag, magnitude(sum(listOf(a.copy(), b.copy()))))
            }
        }
        return mag
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
