fun main() {

    fun parse(input: List<String>): List<List<Int>> = input.map { it.asIterable().map { it.toString().toInt() } }

    fun lowPoints(matrix: List<List<Int>>): List<Pair<Int, Int>> = buildList {
        val maxY = matrix.size - 1
        val maxX = matrix.first().size - 1

        matrix.forEachIndexed { y, row ->
            row.forEachIndexed { x, n ->
                val l = if (x > 0) matrix[y][x - 1] > n else true
                val r = if (x < maxX) matrix[y][x + 1] > n else true
                val t = if (y > 0) matrix[y - 1][x] > n else true
                val b = if (y < maxY) matrix[y + 1][x] > n else true
                if (t && b && l && r) {
                    add(y to x)
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val matrix = parse(input)
        val lowPoints = lowPoints(matrix)
        var sum = 0
        lowPoints.forEach { (y, x) ->
            sum += matrix[y][x] + 1
        }
        return sum
    }

    fun findBasinSize(matrix: List<List<Int>>, point: Pair<Int, Int>): Int {
        val maxY = matrix.size - 1
        val maxX = matrix.first().size - 1

        val basin = mutableSetOf(point)
        val stack = mutableListOf(point)

        while (stack.isNotEmpty()) {
            val (y, x) = stack.removeFirst()
            val n = matrix[y][x]

            if (x > 0 && matrix[y][x - 1] > n && matrix[y][x - 1] != 9) {
                val new = Pair(y, x - 1)
                if (basin.add(new)) stack.add(new)
            }
            if (x < maxX && matrix[y][x + 1] > n && matrix[y][x + 1] != 9) {
                val new = Pair(y, x + 1)
                if (basin.add(new)) stack.add(new)
            }
            if (y > 0 && matrix[y - 1][x] > n && matrix[y - 1][x] != 9) {
                val new = Pair(y - 1, x)
                if (basin.add(new)) stack.add(new)
            }
            if (y < maxY && matrix[y + 1][x] > n && matrix[y + 1][x] != 9) {
                val new = Pair(y + 1, x)
                if (basin.add(new)) stack.add(new)
            }
        }

        return basin.size
    }

    fun part2(input: List<String>): Int {
        val matrix = parse(input)
        val lowPoints = lowPoints(matrix)

        val basins = lowPoints
            .map { coords ->
                findBasinSize(matrix, coords)
            }
            .sortedDescending()
            .take(3)
        val result = basins.fold(1) { acc, n -> acc * n }

        return result
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
