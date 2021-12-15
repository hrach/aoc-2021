import java.util.*

sealed interface Fold {
    class X(val x: Int) : Fold
    class Y(val y: Int) : Fold
}

typealias Grid = TreeMap<Int, TreeMap<Int, Unit>>
typealias Folds = List<Fold>

fun parse2(input: List<String>): Pair<Grid, Folds> {
    val folds = mutableListOf<Fold>()
    val grid = TreeMap<Int, TreeMap<Int, Unit>>()

    input.forEach { line ->
        if (line.isBlank()) return@forEach

        if (line.contains('=')) {
            val dataLine = line.removePrefix("fold along ")
            val axis = dataLine.first()
            val n = dataLine.drop(2).toInt()
            folds += when (axis) {
                'x' -> Fold.X(n)
                'y' -> Fold.Y(n)
                else -> error(axis)
            }

        } else {
            val (x, y) = line.split(',')
            grid.getOrPut(y.toInt()) { TreeMap() }[x.toInt()] = Unit
        }
    }

    return grid to folds
}

fun fold(grid: Grid, fold: Fold): Grid {
    val newGrid: Grid = TreeMap()
    when (fold) {
        is Fold.Y -> {
            for ((y, row) in grid) {
                if (y == fold.y) {
                    continue
                } else if (y > fold.y) {
                    val newY = fold.y - (y - fold.y)
                    newGrid[newY] = TreeMap(row + (grid[newY] ?: emptyMap()))
                } else {
                    newGrid[y] = row
                }
            }
        }
        is Fold.X -> {
            grid.forEach { row ->
                val newRow: TreeMap<Int, Unit> = TreeMap()
                for ((x, col) in row.value) {
                    if (x == fold.x) {
                        continue
                    } else if (x > fold.x) {
                        newRow[fold.x - (x - fold.x)] = col
                    } else {
                        newRow[x] = col
                    }
                }
                newGrid[row.key] = newRow
            }
        }
    }
    return newGrid
}

fun Grid.print() {
    println(
        (0..this.keys.maxOf { it }).map { y ->
            (0..(this[y]?.keys?.maxOf { it } ?: 0)).map { x->
                when (this[y]?.get(x)) {
                    Unit -> '#'
                    else -> '.'
                }
            }.joinToString("")
        }.joinToString("\n")
    )
}
fun main() {

    fun part1(input: List<String>): Int {
        val (grid, folds) = parse2(input)
        val finalGrid = fold(grid, folds.first())
        return finalGrid.values.sumOf { it.values.count() }
    }

    fun part2(input: List<String>): Int {
        val (grid, folds) = parse2(input)
        folds.fold(grid) {acc, fold -> fold(acc, fold) }.print()
        return 0
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    check(part2(testInput) == 0)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
