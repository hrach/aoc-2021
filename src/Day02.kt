fun main() {
    fun part1(input: List<String>): Int {
        var depth = 0
        var horizontal = 0
        input.forEach { line ->
            val (command, number) = line.split(' ')
            when (command) {
                "forward" -> horizontal += number.toInt()
                "up" -> depth -= number.toInt()
                "down" -> depth += number.toInt()
            }
        }
        return depth * horizontal
    }

    fun part2(input: List<String>): Int {
        var depth = 0
        var horizontal = 0
        var aim = 0
        input.forEach { line ->
            val (command, number) = line.split(' ')
            when (command) {
                "forward" -> {
                    horizontal += number.toInt()
                    depth += aim * number.toInt()
                }
                "up" -> aim -= number.toInt()
                "down" -> aim += number.toInt()
            }
        }
        return depth * horizontal
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
