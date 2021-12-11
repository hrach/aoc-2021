fun main() {
    fun parse(input: List<String>): MutableList<MutableList<Int>> =
        input.map { it.toCharArray().map { it.toString().toInt() }.toMutableList() }.toMutableList()

    fun MutableList<MutableList<Int>>.safeInc(y: Int, x: Int) {
        if (y < 0 || y > 9) return
        if (x < 0 || x > 9) return
        if (this[y][x] == 0) return // already flashed
        this[y][x] += 1
    }

    fun evolve(matrix: MutableList<MutableList<Int>>) {
        val cols = matrix.first().indices
        for (y in matrix.indices) {
            for (x in cols) {
                matrix[y][x] += 1
            }
        }
        while (matrix.any { it.any { it > 9 } }) {
            for (y in matrix.indices) {
                for (x in cols) {
                    if (matrix[y][x] > 9) {
                        matrix.safeInc(y - 1, x - 1)
                        matrix.safeInc(y - 1, x)
                        matrix.safeInc(y - 1, x + 1)
                        matrix.safeInc(y, x - 1)
                        matrix.safeInc(y, x + 1)
                        matrix.safeInc(y + 1, x - 1)
                        matrix.safeInc(y + 1, x)
                        matrix.safeInc(y + 1, x + 1)
                        matrix[y][x] = 0
                    }
                }
            }
        }
    }

    fun print(matrix: List<List<Int>>) {
        println(
            matrix.joinToString("\n") { it.joinToString("") }
        )
    }

    fun part1(input: List<String>): Int {
        val matrix = parse(input)
        var flashes = 0
        repeat(100) {
            evolve(matrix)
            flashes += matrix.sumOf { it.count { it == 0 } }

//            println()
//            println("After step ${it + 1}:")
//            print(matrix)
        }

        return flashes
    }

    fun part2(input: List<String>): Int {
        val matrix = parse(input)
        var step = 0
        while (true) {
            evolve(matrix)
            step += 1
            val flashes = matrix.sumOf { it.count { it == 0 } }
            if (flashes == 100) {
                return step
            }
        }
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
