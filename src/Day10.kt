sealed interface Result {
    object Ok : Result
    class Illegal(val char: Char) : Result
    class Incomplete(val missingClosing: List<Char>) : Result
}

val pairs = mapOf(
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<',
)

fun main() {
    fun findIllegal(input: String): Result {
        val openChars = pairs.values
        val closeChars = pairs.keys

        val stack = mutableListOf<Char>()
        input.toCharArray().forEach { char ->
            if (char in openChars) {
                stack.add(char)
            } else {
                check(char in closeChars)
                val last = stack.last()
                val expected = pairs[char]
                if (expected != last) {
                    return Result.Illegal(char)
                }
                stack.removeLast()
            }
        }
        return if (stack.isNotEmpty()) {
            Result.Incomplete(stack)
        } else {
            Result.Ok
        }
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::findIllegal)
            .filterIsInstance<Result.Illegal>()
            .sumOf {
                val r: Int = when (it.char) {
                    ')' -> 3
                    ']' -> 57
                    '}' -> 1197
                    '>' -> 25137
                    else -> 0
                }
                r
            }
    }

    fun part2(input: List<String>): Long {
        val pairs: Map<Char, Char> = pairs.map { it.value to it.key }.toMap()
        val scores = input
            .map(::findIllegal)
            .filterIsInstance<Result.Incomplete>()
            .map {
                val missing = it.missingClosing.reversed().map { pairs[it] }
                val score = missing.fold(0L) { acc, c ->
                    acc * 5 + when (c) {
                        ')' -> 1
                        ']' -> 2
                        '}' -> 3
                        '>' -> 4
                        else -> error(c ?: "")
                    }
                }
                score
            }
            .sorted()

        return scores[scores.size / 2]
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
