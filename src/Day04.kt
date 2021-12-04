fun main() {
    class Board(
        val data: MutableList<Int?>,
    ) {
        fun hasWin(): Boolean {
            for (x in 0..4) {
                var winnerX = true
                var winnerY = true
                for (y in 0..4) {
                    winnerX = winnerX && data[y * 5 + x] == null
                    winnerY = winnerY && data[x * 5 + y] == null
                }
                if (winnerX || winnerY) return true
            }
            return false
        }

        fun mark(draw: Int) {
            val indexOf = data.indexOf(draw)
            if (indexOf != -1) {
                data[indexOf] = null
            }
        }

        fun getRemainingSum(): Int = data.filterNotNull().sum()
    }

    fun parseBoards(data: MutableList<String>): List<Board> = buildList {
        while (data.isNotEmpty()) {
            val numbers = buildList {
                repeat(5) {
                    addAll(data.removeFirst().trim().split("\\s+".toRegex()).map { it.toInt() })
                }
                data.removeFirstOrNull()
            }
            add(Board(numbers.toMutableList()))
        }
    }

    fun part1(input: List<String>): Int {
        val data = input.toMutableList()
        val draws = data.removeFirst().split(',').map { it.toInt() }.toMutableList()
        data.removeFirst()
        val boards = parseBoards(data)

        while (draws.isNotEmpty()) {
            val draw = draws.removeFirst()
            boards.forEach { board ->
                board.mark(draw)
            }
            boards.find { it.hasWin() }?.let { board ->
                return board.getRemainingSum() * draw
            }
        }
        error("no winner")
    }

    fun part2(input: List<String>): Int {
        val data = input.toMutableList()
        val draws = data.removeFirst().split(',').map { it.toInt() }.toMutableList()
        data.removeFirst()
        val boards = parseBoards(data)

        while (draws.isNotEmpty()) {
            val draw = draws.removeFirst()
            val noWinBoard = boards.find { !it.hasWin() }
            boards.forEach { board ->
                board.mark(draw)
            }
            if (boards.all { it.hasWin() }) {
                return noWinBoard!!.getRemainingSum() * draw
            }
        }
        error("no winner")
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
