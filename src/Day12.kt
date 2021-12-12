data class Node(
    val name: String,
) {
    val edges: MutableList<Node> = mutableListOf()
    val isSmall: Boolean = name.all { it.isLowerCase() }
}

fun parse(input: List<String>): Pair<Node, Node> {
    val nodes = mutableMapOf<String, Node>()

    input.forEach { line ->
        val (a, b) = line.split('-')
        val nodeA = nodes.getOrPut(a) { Node(a) }
        val nodeB = nodes.getOrPut(b) { Node(b) }
        nodeA.edges.add(nodeB)
        nodeB.edges.add(nodeA)
    }

    return nodes["start"]!! to nodes["end"]!!
}

fun findPaths(start: Node, end: Node): Set<List<Node>> = buildSet {
    val stack = ArrayDeque(listOf(listOf(start)))
    while (stack.isNotEmpty()) {
        val todo = stack.removeFirst()
        val last = todo.last()
        last.edges.forEach { new ->
            if (new.isSmall && todo.contains(new)) {
                // do nothing, not viable solution
            } else if (new == end) {
                add(todo + new)
            } else {
                stack.add(todo + new)
            }
        }
    }
}

fun findPaths2(start: Node, end: Node): Set<List<Node>> = buildSet {
    val stack = ArrayDeque(listOf(listOf(start)))
    while (stack.isNotEmpty()) {
        val todo = stack.removeFirst()
        val last = todo.last()
        last.edges.forEach { new ->
            val smallCounts = todo.filter { it.isSmall }.groupingBy { it.name }.eachCount()
            val count = smallCounts[new.name] ?: 0

            if (new == start) {
                // do not add start again
            } else if (new.isSmall && !(count == 0 || (count == 1 && smallCounts.all { it.value < 2 }))) {
                // do nothing, not viable solution
            } else if (new == end) {
                add(todo + new)
            } else {
                stack.add(todo + new)
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val graph = parse(input)
        val paths = findPaths(graph.first, graph.second)
        return paths.size
    }

    fun part2(input: List<String>): Int {
        val graph = parse(input)
        val paths = findPaths2(graph.first, graph.second)
        return paths.size
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)
    val testInput2 = readInput("Day12_test2")
    check(part1(testInput2) == 19)
    check(part2(testInput2) == 103)
    val testInput3 = readInput("Day12_test3")
    check(part1(testInput3) == 226)
    check(part2(testInput3) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
